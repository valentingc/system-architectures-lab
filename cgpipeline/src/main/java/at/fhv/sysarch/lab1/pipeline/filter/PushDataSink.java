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
        Color color = input.snd();

        double[] x = new double[]{
                face.getV1().getX(),
                face.getV2().getX(),
                face.getV3().getX()
        };
        double[] y = new double[]{
                face.getV1().getY(),
                face.getV2().getY(),
                face.getV3().getY()
        };

        pipelineData.getGraphicsContext().setStroke(color);
        pipelineData.getGraphicsContext().setFill(color);
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
