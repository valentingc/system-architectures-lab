package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;

/**
 * @author Valentin
 */
public class BackfaceCullingFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private PushPipe<Pair<Face, Color>> outboundPipeline;
    private PipelineData pipelineData;

    public BackfaceCullingFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Pair<Face, Color>> pipe) {
        this.outboundPipeline = pipe;
    }

    @Override
    public PushPipe<Pair<Face, Color>> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public Pair<Face, Color> process(Pair<Face, Color> input) {
        Face face = input.fst();
        if (pipelineData.getViewingEye().dot(face.getN1().toVec3()) > 0) {
            return null;
        }
        return input;
    }

    @Override
    public void write(Pair<Face, Color> input) {
        Pair<Face, Color> result = process(input);
        if (result == null) {
            return;
        }

        if (this.outboundPipeline != null) {
            this.outboundPipeline.write(result);
        }
    }

}
