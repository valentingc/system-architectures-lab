package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PullFilter;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public interface PullPipeInterface<T> {

    T read();

    PullFilter<T, ?> getPreviousFilter();

    void setPreviousFilter(PullFilter<T, ?> previousFilter);
}
