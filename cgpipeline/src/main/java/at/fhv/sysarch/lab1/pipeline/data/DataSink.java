package at.fhv.sysarch.lab1.pipeline.data;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.filter.PullFilter;
import at.fhv.sysarch.lab1.pipeline.filter.PushFilter;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import javafx.scene.paint.Color;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class DataSink implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;
    private PullPipe<Pair<Face, Color>> inboundPipeline;

    public DataSink(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    public DataSink(PipelineData pipelineData, PullPipe<Pair<Face, Color>> inboundPipeline) {
        this.pipelineData = pipelineData;
        this.inboundPipeline = inboundPipeline;
    }

    @Override
    public Pair<Face, Color> read() {
        Pair<Face, Color> input = inboundPipeline.read();
        if (null != input) {
            process(input);
        }

        // No return value needed - rendering only
        return null;
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
    public PullPipe<Pair<Face, Color>> getInboundPipeline() {
        return null;
    }

    @Override
    public void setInboundPipeline(PullPipe<Pair<Face, Color>> pipe) {
        this.inboundPipeline = pipe;
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
