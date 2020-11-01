package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;

/**
 * @author Valentin
 */
public class PushDataSink implements PushFilter<Face, Face> {

    private PipelineData pd;

    public PushDataSink(PipelineData pipelineData) {
        this.pd = pipelineData;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Face> pipe) {
        return;
    }

    @Override
    public PushPipe<Face> getOutboundPipeline() {
        return null;
    }

    @Override
    public void write(Face face) {
        pd.getGraphicsContext().setStroke(pd.getModelColor());

        double[] x = new double[] {
            face.getV1().getX() * 100 + 400,
            face.getV2().getX() * 100 + 400,
            face.getV3().getX() * 100 + 400
        };
        double[] y = new double[] {
            face.getV1().getY() * -100 + 400,
            face.getV2().getY() * -100 + 400,
            face.getV3().getY() * -100 + 400
        };
        pd.getGraphicsContext().strokePolygon(x, y, x.length);

        return;
    }
}
