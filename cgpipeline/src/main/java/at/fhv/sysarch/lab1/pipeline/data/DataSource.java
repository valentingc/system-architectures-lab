package at.fhv.sysarch.lab1.pipeline.data;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.obj.Model;
import at.fhv.sysarch.lab1.pipeline.filter.PullFilter;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import com.hackoeur.jglm.Vec4;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class DataSource implements PullFilter<Face, Face> {
    private Model model;
    private int currentFaceIndex;

    public DataSource(Model model) {
        this.model = model;
    }

    @Override
    public Face read() {
        if (currentFaceIndex >= model.getFaces().size()) {
            // Special marker face to indicate end of data
            return new Face(
                    Vec4.VEC4_ZERO,
                    Vec4.VEC4_ZERO,
                    Vec4.VEC4_ZERO,
                    Vec4.VEC4_ZERO,
                    Vec4.VEC4_ZERO,
                    Vec4.VEC4_ZERO
            );
        }

        return model.getFaces().get(currentFaceIndex++);
    }

    @Override
    public Face process(Face input) {
        // No processing needed
        return input;
    }

    @Override
    public PullPipe<Face> getInboundPipeline() {
        // Start of everything
        return null;
    }

    @Override
    public void setInboundPipeline(PullPipe<Face> pipe) {
        // Intentionally empty
    }

    public void setModel(Model model) {
        this.model = model;
        this.currentFaceIndex = 0;
    }
}
