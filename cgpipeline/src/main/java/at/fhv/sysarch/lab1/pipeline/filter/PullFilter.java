package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public interface PullFilter<T, S> {

    T read();

    T process(S input);

    PullPipe<S> getInboundPipeline();

    void setInboundPipeline(PullPipe<S> pipe);
}
