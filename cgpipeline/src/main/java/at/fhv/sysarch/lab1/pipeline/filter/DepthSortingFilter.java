package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.PipelineData;
import at.fhv.sysarch.lab1.pipeline.Util;
import at.fhv.sysarch.lab1.pipeline.data.Pair;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import javafx.scene.paint.Color;

import java.util.PriorityQueue;
import java.util.Queue;

public class DepthSortingFilter implements PullFilter<Pair<Face, Color>, Pair<Face, Color>> {
    private final PipelineData pipelineData;
    private PullPipe<Pair<Face, Color>> inboundPipeline;
    private Queue<Pair<Face, Color>> processQueue;

    public DepthSortingFilter(PipelineData pipelineData) {
        this.pipelineData = pipelineData;
    }

    @Override
    public Pair<Face, Color> read() {
        this.processQueue = new PriorityQueue<>((firstPair, secondPair) -> {
            // First pair
            float firstZ1 = firstPair.fst().getV1().getZ();
            float firstZ2 = firstPair.fst().getV2().getZ();
            float firstZ3 = firstPair.fst().getV3().getZ();

            float firstPairAverageZ = (firstZ1 + firstZ2 + firstZ3) / 3;

            // Second pair
            float secondZ1 = secondPair.fst().getV1().getZ();
            float secondZ2 = secondPair.fst().getV2().getZ();
            float secondZ3 = secondPair.fst().getV3().getZ();

            float secondPairAverageZ = (secondZ1 + secondZ2 + secondZ3) / 3;

            // Calculation
            return (int) (firstPairAverageZ - secondPairAverageZ);
        });

        while (true) {
            Pair<Face, Color> input = inboundPipeline.read();
            if (Util.isFaceMakingEnd(input.fst())) {
                break;
            }

            this.processQueue.add(process(input));
        }

        // TODO - Utterly broken
        return null;
    }

    @Override
    public Pair<Face, Color> process(Pair<Face, Color> input) {
        return this.processQueue.poll();
    }

    @Override
    public PullPipe<Pair<Face, Color>> getInboundPipeline() {
        return inboundPipeline;
    }

    @Override
    public void setInboundPipeline(PullPipe<Pair<Face, Color>> pipe) {
        this.inboundPipeline = pipe;
    }
}
