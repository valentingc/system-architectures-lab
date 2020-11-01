package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PushFilter;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class PushPipe<T> {
    private PushFilter<T, ?> nextFilter;

    public PushPipe(PushFilter<T, ?> nextFilter) {
        this.nextFilter = nextFilter;
    }

    public void write(T data) {
        this.nextFilter.write(data);
    }

    public PushFilter<T, ?> getNextFilter() {
        return this.nextFilter;
    }

    public void setNextFilter(PushFilter<T, ?> nextFilter) {
        this.nextFilter = nextFilter;
    }
}
