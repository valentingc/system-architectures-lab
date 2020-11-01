package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;
import at.fhv.sysarch.lab1.utils.MatrixUtils;
import com.hackoeur.jglm.Mat3;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

/**
 * @author Valentin
 */
public class ModelViewTransformationFilter implements PushFilter<Face, Face> {
    private PushPipe<Face> outboundPipeline;
    private PipelineData pipelineData;
    private Mat4 viewTransform;
    private Face face;

    public ModelViewTransformationFilter(Mat4 viewTransform, PipelineData pipelineData) {
        this.viewTransform = viewTransform;
        this.pipelineData = pipelineData;
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
//        Mat4 faceMat4 = new Mat4(face.getV1(), face.getV2(), face.getV3(), new Vec4(0,0,0,1));
//        faceMat4 = faceMat4.multiply(viewTransform);
//
//        Face result = new Face(faceMat4.getColumn(0), faceMat4.getColumn(1),
//            faceMat4.getColumn(2),this.face.getN1(),
//            this.face.getN2(),
//            this.face.getN3());


////        // the model position in WORLD space (for this example its 0/0/0 in all cases)
//        Vec4 pos = new Vec4(
//            pipelineData.getModelPos().getX(),
//            pipelineData.getModelPos().getY(),
//            pipelineData.getModelPos().getZ(),
//            0);
//
//        // the position of the camera in WORLD space
//        Vec4 viewingEye = new Vec4(
//            pipelineData.getViewingEye().getX(),
//            pipelineData.getViewingEye().getY(),
//            pipelineData.getViewingEye().getZ(),
//            0);
//
//        Face result =
//            new Face(
//                pipelineData.getViewportTransform().multiply(face.getV1().add(pos).add(viewingEye)).multiply(0.5f),
//                pipelineData.getViewportTransform().multiply(face.getV2().add(pos).add(viewingEye)).multiply(0.5f),
//                pipelineData.getViewportTransform().multiply(face.getV3().add(pos).add(viewingEye)).multiply(0.5f),
//                pipelineData.getViewportTransform().multiply(face.getN1().add(pos).add(viewingEye)).multiply(0.5f),
//                pipelineData.getViewportTransform().multiply(face.getN2().add(pos).add(viewingEye)).multiply(0.5f),
//                pipelineData.getViewportTransform().multiply(face.getN3().add(pos).add(viewingEye)).multiply(0.5f)
//            );
        // viewTransform X rotationmatrix = output ???
        // replace face v1-4 with output v1-v4???

        if (this.outboundPipeline != null) {
            this.outboundPipeline.write(input);
        }
    }

    public Mat4 getViewTransform() {
        return viewTransform;
    }

    public void setViewTransform(Mat4 viewTransform) {
        this.viewTransform = viewTransform;
    }

}
