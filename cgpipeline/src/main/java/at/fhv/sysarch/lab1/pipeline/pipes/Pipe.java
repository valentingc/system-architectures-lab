package at.fhv.sysarch.lab1.pipeline.pipes;

import at.fhv.sysarch.lab1.pipeline.filter.PushFilter;

public interface Pipe<T> {

    void setNextFilter(PushFilter<T, T> nextFilter);
    PushFilter<T, T>  getNextFilter();

    void write(T data);
}
