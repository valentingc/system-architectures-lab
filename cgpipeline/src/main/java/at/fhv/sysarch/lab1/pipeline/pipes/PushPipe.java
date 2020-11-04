package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PushFilter;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class PushPipe<T> implements PushPipeInterface<T> {
    private PushFilter<T, ?> nextFilter;

    public PushPipe(PushFilter<T, ?> nextFilter) {
        this.nextFilter = nextFilter;
    }

    @Override
    public void write(T data) {
        this.nextFilter.write(data);
    }

    @Override
    public PushFilter<T, ?> getNextFilter() {
        return this.nextFilter;
    }

    @Override
    public void setNextFilter(PushFilter<T, ?> nextFilter) {
        this.nextFilter = nextFilter;
    }
}
