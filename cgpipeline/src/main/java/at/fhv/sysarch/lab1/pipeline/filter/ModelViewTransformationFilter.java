package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;

/**
 * @author Valentin
 */
public class ModelViewTransformationFilter implements PushFilter<Face, Face> {
    private Pipe<Face> outboundPipeline;
    private Mat4 transformationMatrix;

    public ModelViewTransformationFilter(Mat4 transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }

    @Override
    public void setOutboundPipeline(Pipe<Face> pipe) {
        this.outboundPipeline = pipe;
    }

    @Override
    public Pipe<Face> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public void write(Face input) {
        // TODO: transform the model
        //Matrices.rotate()
        this.outboundPipeline.write(input);
    }
}
