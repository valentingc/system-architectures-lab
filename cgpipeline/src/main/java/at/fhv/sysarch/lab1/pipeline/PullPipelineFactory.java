package at.fhv.sysarch.lab1.pipeline;

import at.fhv.sysarch.lab1.animation.AnimationRenderer;
import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.obj.Model;
import at.fhv.sysarch.lab1.pipeline.data.DataSink;
import at.fhv.sysarch.lab1.pipeline.data.DataSource;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.filter.*;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PullPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        // pull from the source (model)
        DataSource dataSource = new DataSource(pd.getModel());

        // 1. perform model-view transformation from model to VIEW SPACE coordinates
        ModelViewTransformationFilter modelViewFilter = new ModelViewTransformationFilter(pd);
        PullPipe<Face> dataToModelViewPipe = new PullPipe<>(dataSource);
        modelViewFilter.setInboundPipeline(dataToModelViewPipe);

        // 2. perform backface culling in VIEW SPACE
        BackfaceCullingFilter backfaceCullingFilter = new BackfaceCullingFilter();
        PullPipe<Face> modelViewToBackfacePipe = new PullPipe<>(modelViewFilter);
        backfaceCullingFilter.setInboundPipeline(modelViewToBackfacePipe);

        // 3. perform depth sorting in VIEW SPACE
        DepthSortingFilter depthSortingFilter = new DepthSortingFilter();
        PullPipe<Face> backfaceToDepthPipe = new PullPipe<>(backfaceCullingFilter);
        depthSortingFilter.setInboundPipeline(backfaceToDepthPipe);

        // 4. add coloring (space unimportant)
        ColorFilter colorFilter = new ColorFilter(pd);
        PullPipe<Face> depthSortingToColorPipe = new PullPipe<>(depthSortingFilter);
        colorFilter.setInboundPipeline(depthSortingToColorPipe);

        // lighting can be switched on/off
        PerspectiveProjectionFilter perspectiveProjectionFilter = new PerspectiveProjectionFilter(pd);
        if (pd.isPerformLighting()) {
            // 4a. perform lighting in VIEW SPACE
            LightingFilter lightingFilter = new LightingFilter(pd);
            PullPipe<Pair<Face, Color>> colorToLightingPipe = new PullPipe<>(colorFilter);
            lightingFilter.setInboundPipeline(colorToLightingPipe);

            // 5. perform projection transformation on VIEW SPACE coordinates
            PullPipe<Pair<Face, Color>> lightingToPerspectivePipe = new PullPipe<>(lightingFilter);
            perspectiveProjectionFilter.setInboundPipeline(lightingToPerspectivePipe);
        } else {
            // 5. perform projection transformation
            PullPipe<Pair<Face, Color>> lightingToPerspectivePipe = new PullPipe<>(colorFilter);
            perspectiveProjectionFilter.setInboundPipeline(lightingToPerspectivePipe);
        }

        // 6. perform perspective division to screen coordinates
        ScreenSpaceTransformationFilter screenSpaceTransformationFilter = new ScreenSpaceTransformationFilter(pd);
        PullPipe<Pair<Face, Color>> perspectiveToScreenSpacePipe = new PullPipe<>(perspectiveProjectionFilter);
        screenSpaceTransformationFilter.setInboundPipeline(perspectiveToScreenSpacePipe);

        // 7. feed into the sink (renderer)
        DataSink dataSink = new DataSink(pd);
        PullPipe<Pair<Face, Color>> screenSpaceToSinkPipe = new PullPipe<>(screenSpaceTransformationFilter);
        dataSink.setInboundPipeline(screenSpaceToSinkPipe);

        // returning an animation renderer which handles clearing of the
        // viewport and computation of the praction
        return new AnimationRenderer(pd) {
            // rotation variable goes in here
            float rotation = 0f;

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer). 
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render 
             */
            @Override
            protected void render(float fraction, Model model) {
                // compute rotation in radians
                rotation += fraction;
                double radiant = rotation % (2 * Math.PI); // 2 PI = 360°

                // create new model rotation matrix using pd.modelRotAxis
                Mat4 rotationMatrix = Matrices.rotate(
                        (float) radiant,
                        pd.getModelRotAxis() // Rotation axis is a Vec3 with y=1 and x/z=0
                );

                // update model-view filter
                modelViewFilter.setRotationMatrix(rotationMatrix);

                // trigger rendering of the pipeline
                dataSource.setModel(model);
                dataSink.read();
            }
        };
    }
}
