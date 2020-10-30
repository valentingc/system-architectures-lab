package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.pipes.Pipe;
import at.fhv.sysarch.lab1.pipeline.pipes.PushPipe;

public interface PushFilter<T1, T2> {

    void setOutboundPipeline(PushPipe<T1> pipe);
    PushPipe<T2> getOutboundPipeline();

    // Write to Outbound pipe
    void write(T1 input);
}
