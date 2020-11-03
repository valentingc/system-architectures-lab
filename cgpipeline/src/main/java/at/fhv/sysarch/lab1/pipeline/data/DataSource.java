package at.fhv.sysarch.lab1.pipeline.data;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.obj.Model;
import at.fhv.sysarch.lab1.pipeline.filter.PullFilter;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class DataSource implements PullFilter<Face, Face> {
    private final Model model;
    private int currentFaceIndex;

    public DataSource(Model model) {
        this.model = model;
    }

    @Override
    public Face read() {
        if (currentFaceIndex >= model.getFaces().size()) {
            return null;
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
}
