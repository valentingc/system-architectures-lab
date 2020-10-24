package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PushFilter;

/**
 * @author Valentin
 */
public class PushPipe<T> {

    private PushFilter<T, ?> nextFilter;

    public PushPipe(PushFilter<T, ?> nextFilter) {
        this.nextFilter = nextFilter;
    }

    public void setNextFilter(PushFilter<T, ?> nextFilter) {
        this.nextFilter = nextFilter;
    }

    public PushFilter<?, ?> getNextFilter() {
        return this.nextFilter;
    }

    public void write(T data) {
        this.nextFilter.write(data);

    }
}
