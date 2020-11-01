package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import com.hackoeur.jglm.Vec2;
import javafx.scene.paint.Color;

/**
 * @author Valentin
 */
public class PushDataSink implements PushFilter<Pair<Face, Color>, Pair<Face, Color>> {

    private PipelineData pd;

    public PushDataSink(PipelineData pipelineData) {
        this.pd = pipelineData;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Pair<Face, Color>> pipe) {
        return;
    }

    @Override
    public PushPipe<Pair<Face, Color>> getOutboundPipeline() {
        return null;
    }

    @Override
    public void write(Pair<Face, Color> pair) {
        Face face = pair.fst();
        Vec2 x1Screen = face.getV1().toScreen();
        Vec2 x2Screen = face.getV2().toScreen();
        Vec2 x3Screen = face.getV3().toScreen();

        double[] x = new double[] {
            face.getV1().getX() * 100 + 400, // * 100 + 400
            face.getV2().getX() * 100 + 400,
            face.getV3().getX() * 100 + 400
        };
        double[] y = new double[] {
            face.getV1().getY() * -100 + 400,
            face.getV2().getY() * -100 + 400,
            face.getV3().getY() * -100 + 400
        };
//        double[] x = new double[] {x1Screen.getX() * 100 + 400, x2Screen.getX() * 100 + 400,
//            x3Screen.getX() * 100 + 400};
//        double[] y = new double[] {x1Screen.getY() * -100 + 400, x2Screen.getY() * -100 + 400,
//            x3Screen.getY() * -100 + 400};



        pd.getGraphicsContext().setStroke(pd.getModelColor());
        pd.getGraphicsContext().setFill(pd.getModelColor());
        switch (pd.getRenderingMode()) {
            case POINT:
                pd.getGraphicsContext().fillOval(x[0],y[0],5,5);
                break;
            case FILLED:
                pd.getGraphicsContext().fillPolygon(x,y,x.length);
                pd.getGraphicsContext().strokePolygon(x,y,x.length);
                //filled is twice
                break;
            case WIREFRAME:
                pd.getGraphicsContext().strokePolygon(x,y,x.length);
                break;
        }
        return;
    }
}
