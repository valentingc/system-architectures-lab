package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PushFilter;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public interface Pipe<T> {

    void write(T data);

    PushFilter<T, ?> getNextFilter();

    void setNextFilter(PushFilter<T, ?> nextFilter);
}
