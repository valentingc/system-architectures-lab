package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class BackfaceCullingFilter implements PushFilter<Face, Face>, PullFilter<Face, Face> {
    private PullPipe<Face> inboundPipeline;
    private PushPipe<Face> outboundPipeline;

    @Override
    public Face read() {
        Face input = inboundPipeline.read();
        if (null == input) {
            return null;
        }

        return process(input);
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
    public Face process(Face face) {
        if (face.getV1().dot(face.getN1()) > 0) {
            return null;
        }
        return face;
    }

    @Override
    public PullPipe<Face> getInboundPipeline() {
        return inboundPipeline;
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
}
