package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.Util;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import javafx.scene.paint.Color;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class ScreenSpaceTransformationFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;
    private PullPipe<Pair<Face, Color>> inboundPipeline;
    private PushPipe<Pair<Face, Color>> outboundPipeline;

    public ScreenSpaceTransformationFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public Pair<Face, Color> read() {
        Pair<Face, Color> input = inboundPipeline.read();
        if (null == input) {
            return null;
        } else if (Util.isFaceMakingEnd(input.fst())) {
            return input;
        }

        return process(input);
    }

    @Override
    public void write(Pair<Face, Color> input) {
        Pair<Face, Color> result = process(input);

        if (null != this.outboundPipeline) {
            this.outboundPipeline.write(result);
        }
    }

    @Override
    public Pair<Face, Color> process(Pair<Face, Color> input) {
        Face face = input.fst();

        Face dividedFace = new Face(
                face.getV1().multiply((1.0f / face.getV1().getW())),
                face.getV2().multiply((1.0f / face.getV2().getW())),
                face.getV3().multiply((1.0f / face.getV3().getW())),
                face
        );

        Face transformedFace = new Face(
                pipelineData.getViewportTransform().multiply(dividedFace.getV1()),
                pipelineData.getViewportTransform().multiply(dividedFace.getV2()),
                pipelineData.getViewportTransform().multiply(dividedFace.getV3()),
                dividedFace
        );

        return new Pair<>(transformedFace, input.snd());
    }

    @Override
    public PullPipe<Pair<Face, Color>> getInboundPipeline() {
        return inboundPipeline;
    }

    @Override
    public void setInboundPipeline(PullPipe<Pair<Face, Color>> pipe) {
        inboundPipeline = pipe;
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
