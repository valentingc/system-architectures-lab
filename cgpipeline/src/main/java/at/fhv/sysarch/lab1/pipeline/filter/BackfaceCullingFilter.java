package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Mat4;

/**
 * @author Valentin
 */
public class BackfaceCullingFilter implements PushFilter<Face, Face> {
    private PushPipe<Face> outboundPipeline;
    private PipelineData pipelineData;

    public BackfaceCullingFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Face> pipe) {
        this.outboundPipeline = pipe;
    }

    @Override
    public PushPipe<Face> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public void write(Face input) {
        if (pipelineData.getViewingEye().dot(input.getN1().toVec3()) > 0) {
            return;
        }

        if (this.outboundPipeline != null) {
            this.outboundPipeline.write(input);
        }
    }

}
