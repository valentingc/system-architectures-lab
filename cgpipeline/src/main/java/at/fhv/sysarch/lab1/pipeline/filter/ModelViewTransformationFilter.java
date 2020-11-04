package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.Util;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Mat4;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class ModelViewTransformationFilter implements PushFilter<Face, Face>, PullFilter<Face, Face> {
    private final PipelineData pipelineData;
    private PullPipe<Face> inboundPipeline;
    private PushPipe<Face> outboundPipeline;
    private Mat4 rotationMatrix;

    public ModelViewTransformationFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public Face read() {
        Face input = inboundPipeline.read();
        if (null == input) {
            return null;
        } else if (Util.isFaceMakingEnd(input)) {
            return input;
        }

        return process(input);
    }

    @Override
    public void write(Face input) {
        Face result = process(input);
        if (null == result) {
            return;
        }

        if (this.outboundPipeline != null) {
            this.outboundPipeline.write(result);
        }
    }

    @Override
    public Face process(Face face) {
        // compute updated model-view transformation
        Mat4 modelTranslation = pipelineData.getModelTranslation();
        Mat4 viewTransformation = pipelineData.getViewTransform();

        Mat4 updatedTransformation = viewTransformation.multiply(modelTranslation).multiply(rotationMatrix);

        return new Face(
                updatedTransformation.multiply(face.getV1()),
                updatedTransformation.multiply(face.getV2()),
                updatedTransformation.multiply(face.getV3()),
                updatedTransformation.multiply(face.getN1()),
                updatedTransformation.multiply(face.getN2()),
                updatedTransformation.multiply(face.getN3())
        );
    }

    @Override
    public PullPipe<Face> getInboundPipeline() {
        return this.inboundPipeline;
    }

    @Override
    public void setInboundPipeline(PullPipe<Face> pipe) {
        this.inboundPipeline = pipe;
    }

    @Override
    public PushPipe<Face> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Face> pipe) {
        this.outboundPipeline = pipe;
    }

    public void setRotationMatrix(Mat4 rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }
}
