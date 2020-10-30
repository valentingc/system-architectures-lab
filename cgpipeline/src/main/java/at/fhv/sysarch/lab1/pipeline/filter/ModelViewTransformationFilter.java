package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import at.fhv.sysarch.lab1.utils.MatrixUtils;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

/**
 * @author Valentin
 */
public class ModelViewTransformationFilter implements PushFilter<Face, Face> {
    private PushPipe<Face> outboundPipeline;
    private Mat4 modelTransform;
    private Mat4 viewTransform;
    private Face face;

    public ModelViewTransformationFilter(Mat4 modelTransform, Mat4 viewTransform) {
        this.modelTransform = modelTransform;
        this.viewTransform = viewTransform;
    }

    @Override
    public void setOutboundPipeline(PushPipe<Face> pipe) {
        this.outboundPipeline = pipe;
    }

    @Override
    public PushPipe<Face> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    @Override
    public void write(Face input) {
        this.face = input;


        // TODO: transform the model

        Mat4 transformedToViewMatrix = this.modelTransform.multiply(this.viewTransform);
        Vec4 v1 = new Vec4(
            transformedToViewMatrix.getColumn(0)
        );
        Vec4 v2 = new Vec4(
            transformedToViewMatrix.getColumn(1)
        );
        Vec4 v3 = new Vec4(
            transformedToViewMatrix.getColumn(2)
        );
        Face result = new Face(v1, v2, v3, this.face.getN1(), this.face.getN2(), this.face.getN3());
        // viewTransform X rotationmatrix = output ???
        // replace face v1-4 with output v1-v4???
        this.outboundPipeline.write(result);
    }
}
