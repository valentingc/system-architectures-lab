package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;

/**
 * @author Valentin
 */
public class PushDataSink implements PushFilter<Face, Face> {
    @Override
    public void setOutboundPipeline(PushPipe<Face> pipe) {
        return;
    }

    @Override
    public PushPipe<Face> getOutboundPipeline() {
        return null;
    }

    @Override
    public void write(Face input) {
        System.out.println("Reached the end ???");
        return;
    }
}
