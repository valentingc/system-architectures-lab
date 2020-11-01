package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;

public class PerspectiveProjectionFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;
    private PushPipe<Pair<Face, Color>> outboundPipeline;

    public PerspectiveProjectionFilter(PipelineData pipelineData) {
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
        Mat4 projectionTransform = pipelineData.getProjTransform();
        Face face = input.fst();

        Face projectedFace = new Face(
                projectionTransform.multiply(face.getV1()),
                projectionTransform.multiply(face.getV2()),
                projectionTransform.multiply(face.getV3()),
                face
        );

        // TODO - This is probably broken?
        return new Pair<>(projectedFace, input.snd());
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
