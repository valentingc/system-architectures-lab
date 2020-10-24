package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.utils.MatrixUtils;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

/**
 * @author Valentin
 */
public class ModelViewTransformationFilter implements PushFilter<Face, Face> {
    private Pipe<Face> outboundPipeline;
    private Mat4 transformationMatrix;
    private Mat4 viewTransform;

    public ModelViewTransformationFilter(Mat4 transformationMatrix, Mat4 viewTransform) {
        this.transformationMatrix = transformationMatrix;
        this.viewTransform = viewTransform;
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
        Mat4 rotationMatrix = Matrices.rotate((float) 0.01, new Vec3(0,1,0));

        // viewTransform X rotationmatrix = output ???
        // replace face v1-4 with output v1-v4???
        this.outboundPipeline.write(input);
    }
}
