package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import javafx.scene.paint.Color;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class PushDataSink implements PushFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;

    public PushDataSink(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public void write(Pair<Face, Color> pair) {
        process(pair);
    }

    @Override
    public Pair<Face, Color> process(Pair<Face, Color> input) {
        Face face = input.fst();
        double[] x = new double[]{
                face.getV1().getX() * 100 + 400,
                face.getV2().getX() * 100 + 400,
                face.getV3().getX() * 100 + 400
        };
        double[] y = new double[]{
                face.getV1().getY() * -100 + 400,
                face.getV2().getY() * -100 + 400,
                face.getV3().getY() * -100 + 400
        };

        pipelineData.getGraphicsContext().setStroke(pipelineData.getModelColor());
        pipelineData.getGraphicsContext().setFill(pipelineData.getModelColor());
        switch (pipelineData.getRenderingMode()) {
            case POINT:
                pipelineData.getGraphicsContext().fillOval(x[0], y[0], 3, 3);
                break;
            case WIREFRAME:
                pipelineData.getGraphicsContext().strokePolygon(x, y, x.length);
                break;
            case FILLED:
                pipelineData.getGraphicsContext().fillPolygon(x, y, x.length);
                pipelineData.getGraphicsContext().strokePolygon(x, y, x.length);
                break;
        }

        // No return value needed - rendering only
        return null;
    }

    @Override
    public PushPipe<Pair<Face, Color>> getOutboundPipeline() {
        return null;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Pair<Face, Color>> pipe) {
        // Intentionally empty
    }
}
