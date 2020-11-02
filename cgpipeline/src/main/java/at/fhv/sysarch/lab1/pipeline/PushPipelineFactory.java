package at.fhv.sysarch.lab1.pipeline;

import at.fhv.sysarch.lab1.animation.AnimationRenderer;
import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.obj.Model;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.filter.*;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PushPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        // TODO: push from the source (model)

        // 1. perform model-view transformation from model to VIEW SPACE coordinates
        ModelViewTransformationFilter modelViewFilter = new ModelViewTransformationFilter(pd);

        // 2. perform backface culling in VIEW SPACE
        BackfaceCullingFilter backfaceCullingFilter = new BackfaceCullingFilter(pd);
        PushPipe<Face> modelViewToBackfacePipe = new PushPipe<>(backfaceCullingFilter);
        modelViewFilter.setOutboundPipeline(modelViewToBackfacePipe);

        // 3. perform depth sorting in VIEW SPACE
        // NOT POSSIBLE WITH A PUSH PIPELINE

        // 4. add coloring (space unimportant)
        ColorFilter colorFilter = new ColorFilter(pd);
        PushPipe<Face> depthSortingToColorPipe = new PushPipe<>(colorFilter);
        backfaceCullingFilter.setOutboundPipeline(depthSortingToColorPipe);

        // lighting can be switched on/off
        PerspectiveProjectionFilter perspectiveProjectionFilter = new PerspectiveProjectionFilter(pd);
        if (pd.isPerformLighting()) {
            // 4a. TODO perform lighting in VIEW SPACE

            // 5. perform projection transformation on VIEW SPACE coordinates
            PushPipe<Pair<Face, Color>> lightingToPerspectivePipe = new PushPipe<>(perspectiveProjectionFilter);
            colorFilter.setOutboundPipeline(lightingToPerspectivePipe); // TODO
        } else {
            // 5. perform projection transformation
            PushPipe<Pair<Face, Color>> colorToPerspectivePipe = new PushPipe<>(perspectiveProjectionFilter);
            colorFilter.setOutboundPipeline(colorToPerspectivePipe);
        }

        // 6. perform perspective division to screen coordinates
        ScreenSpaceTransformationFilter screenSpaceTransformationFilter = new ScreenSpaceTransformationFilter(pd);
        PushPipe<Pair<Face, Color>> lightingToScreenSpacePipe = new PushPipe<>(screenSpaceTransformationFilter);
        perspectiveProjectionFilter.setOutboundPipeline(lightingToScreenSpacePipe);

        // 7. feed into the sink (renderer)
        PushPipe<Pair<Face, Color>> toSinkPipe = new PushPipe<>(new PushDataSink(pd));
        screenSpaceTransformationFilter.setOutboundPipeline(toSinkPipe);

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
                PushPipe<Face> pipe = new PushPipe<>(modelViewFilter);
                model.getFaces().forEach(pipe::write);
            }
        };
    }
}
