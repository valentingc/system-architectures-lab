package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import javafx.scene.paint.Color;

public class LightingFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;
    private PushPipe<Pair<Face, Color>> outboundPipeline;

    public LightingFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public void write(Pair<Face, Color> input) {
        Pair<Face, Color> result = process(input);
        if (null == result) {
            return;
        }

        if (this.outboundPipeline != null) {
            this.outboundPipeline.write(result);
        }
    }

    @Override
    public Pair<Face, Color> process(Pair<Face, Color> input) {
        Face face = input.fst();

        float dotProduct = face.getN1().toVec3().dot(pipelineData.getLightPos().getUnitVector());
        if (dotProduct <= 0) {
            return new Pair<>(face, Color.BLACK);
        }

        return new Pair<>(face, input.snd().deriveColor(0, 1, dotProduct, 1));
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
