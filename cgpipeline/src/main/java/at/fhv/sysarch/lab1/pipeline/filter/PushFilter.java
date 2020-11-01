package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public interface PushFilter<T, S> {

    void write(T input);

    S process(T input);

    PushPipe<S> getOutboundPipeline();

    void setOutboundPipeline(PushPipe<T> pipe);
}
