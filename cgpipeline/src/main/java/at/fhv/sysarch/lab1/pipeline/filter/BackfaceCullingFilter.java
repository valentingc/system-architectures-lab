package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class BackfaceCullingFilter implements PushFilter<Face, Face> {
    private final PipelineData pipelineData;
    private PushPipe<Face> outboundPipeline;

    public BackfaceCullingFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public void write(Face input) {
        Face result = process(input);
        if (null == result) {
            return;
        }

        if (null != this.outboundPipeline) {
            this.outboundPipeline.write(result);
        }
    }

    @Override
    public Face process(Face input) {
        if (pipelineData.getViewingEye().dot(input.getN1().toVec3()) > 0) {
            return null;
        }
        return input;
    }

    @Override
    public PushPipe<Face> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Face> pipe) {
        this.outboundPipeline = pipe;
    }
}
