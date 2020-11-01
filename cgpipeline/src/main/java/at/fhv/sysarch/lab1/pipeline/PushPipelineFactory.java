package at.fhv.sysarch.lab1.pipeline;

import at.fhv.sysarch.lab1.animation.AnimationRenderer;
import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.obj.Model;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.filter.BackfaceCullingFilter;
import at.fhv.sysarch.lab1.pipeline.filter.ModelViewTransformationFilter;
import at.fhv.sysarch.lab1.pipeline.filter.PushDataSink;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec4;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public class PushPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {

        // TODO: push from the source (model)

        // TODO 1. perform model-view transformation from model to VIEW SPACE coordinates



        ModelViewTransformationFilter modelViewFilter =
            new ModelViewTransformationFilter(
                pd.getViewTransform(),
                pd
            );

        BackfaceCullingFilter backfaceCullingFilter = new BackfaceCullingFilter(pd);
        PushPipe<Pair<Face, Color>> modelViewToBackfacePipe = new PushPipe<>(backfaceCullingFilter);
        modelViewFilter.setOutboundPipeline(modelViewToBackfacePipe);

        PushPipe<Pair<Face, Color>> toSinkPipe = new PushPipe<>(new PushDataSink(pd));
        backfaceCullingFilter.setOutboundPipeline(toSinkPipe);

        // MAYBE we need this somewhere..?? -> pd.getModelRotAxis();
        // TODO 2. perform backface culling in VIEW SPACE

        // TODO 3. perform depth sorting in VIEW SPACE

        // TODO 4. add coloring (space unimportant)

        // lighting can be switched on/off
        if (pd.isPerformLighting()) {
            // 4a. TODO perform lighting in VIEW SPACE

            // 5. TODO perform projection transformation on VIEW SPACE coordinates
        } else {
            // 5. TODO perform projection transformation
        }

        // TODO 6. perform perspective division to screen coordinates

        // TODO 7. feed into the sink (renderer)

        // returning an animation renderer which handles clearing of the
        // viewport and computation of the praction
        return new AnimationRenderer(pd) {
            // TODO rotation variable goes in here
            float rotation = 0f;

            /** This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer). 
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render 
             */
            @Override
            protected void render(float fraction, Model model) {
                // TODO compute rotation in radians
                // 2 PI = 360°
                rotation += fraction;
                double radiant = rotation % (2 * Math.PI);

                // Calculate rotation matrix
                // TODO create new model rotation matrix using pd.getModelRotAxis and Matrices.rotate
                // Rotation axis is a vec3 with y=1 end x/z=0
                Mat4 rotationMatrix = Matrices.rotate((float) radiant, pd.getModelRotAxis());

                // TODO compute updated model-view tranformation
                // MODEL X ROTATE
                Mat4 modelTranslation = pd.getModelTranslation();
                Mat4 viewTransFormation = pd.getViewTransform();

                Mat4 viewTransform =
                    viewTransFormation.multiply(modelTranslation).multiply(rotationMatrix);

                // TODO update model-view filter
                modelViewFilter.setViewTransform(viewTransform);

                // TODO trigger rendering of the pipeline
                PushPipe<Pair<Face, Color>> pipe = new PushPipe<>(modelViewFilter);
                model.getFaces().forEach(face -> {
                    // drehen
                    Face newFace = new Face(
                        rotationMatrix.multiply(face.getV1()),
                        rotationMatrix.multiply(face.getV2()),
                        rotationMatrix.multiply(face.getV3()),
                        rotationMatrix.multiply(face.getN1()),
                        rotationMatrix.multiply(face.getN2()),
                        rotationMatrix.multiply(face.getN3())
                    );
                    pipe.write(new Pair<>(newFace, pd.getModelColor()));
                });
            }
        };
    }
}