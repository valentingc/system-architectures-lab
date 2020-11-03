package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import javafx.scene.paint.Color;

public class ColorFilter implements PushFilter<Face, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Face> {
    private final PipelineData pipelineData;
    private PullPipe<Face> inboundPipeline;
    private PushPipe<Pair<Face, Color>> outboundPipeline;

    public ColorFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public Pair<Face, Color> read() {
        Face input = inboundPipeline.read();
        if (null == input) {
            return null;
        }

        return process(input);
    }

    @Override
    public void write(Face input) {
        Pair<Face, Color> result = process(input);

        if (null != this.outboundPipeline) {
            this.outboundPipeline.write(result);
        }
    }

    @Override
    public Pair<Face, Color> process(Face face) {
        return new Pair<>(face, pipelineData.getModelColor());
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
    public PushPipe<Pair<Face, Color>> getOutboundPipeline() {
        return outboundPipeline;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Pair<Face, Color>> pipe) {
        this.outboundPipeline = pipe;
    }
}
