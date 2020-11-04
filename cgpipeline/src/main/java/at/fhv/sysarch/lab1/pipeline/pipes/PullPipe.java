package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PullFilter;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class PullPipe<T> implements PullPipeInterface<T> {
    private PullFilter<T, ?> previousFilter;

    public PullPipe(PullFilter<T, ?> previousFilter) {
        this.previousFilter = previousFilter;
    }

    @Override
    public T read() {
        return this.previousFilter.read();
    }

    @Override
    public PullFilter<T, ?> getPreviousFilter() {
        return this.previousFilter;
    }

    @Override
    public void setPreviousFilter(PullFilter<T, ?> previousFilter) {
        this.previousFilter = previousFilter;
    }
}
