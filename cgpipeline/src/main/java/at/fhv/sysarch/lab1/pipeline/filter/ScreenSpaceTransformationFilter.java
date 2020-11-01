package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Vec4;
import javafx.scene.paint.Color;

public class ScreenSpaceTransformationFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;
    private PushPipe<Pair<Face, Color>> outboundPipeline;

    public ScreenSpaceTransformationFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
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
                new Vec4(
                        face.getV1().getX() / face.getV1().getZ(),
                        face.getV1().getY() / face.getV1().getZ(),
                        face.getV1().getZ() / face.getV1().getZ(),
                        face.getV1().getZ()
                ),
                new Vec4(
                        face.getV2().getX() / face.getV2().getZ(),
                        face.getV2().getY() / face.getV2().getZ(),
                        face.getV2().getZ() / face.getV2().getZ(),
                        face.getV2().getZ()
                ),
                new Vec4(
                        face.getV3().getX() / face.getV3().getZ(),
                        face.getV3().getY() / face.getV3().getZ(),
                        face.getV3().getZ() / face.getV3().getZ(),
                        face.getV3().getZ()
                ),
                face
        );

        Face transformedFace = new Face(
                pipelineData.getViewportTransform().multiply(dividedFace.getV1()),
                pipelineData.getViewportTransform().multiply(dividedFace.getV2()),
                pipelineData.getViewportTransform().multiply(dividedFace.getV3()),
                dividedFace
        );

        return input;
        // TODO - This filter is utterly broken!
        // return new Pair<>(transformedFace, input.snd());
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
