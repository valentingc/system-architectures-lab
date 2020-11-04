package at.fhv.sysarch.lab1.pipeline.filter;

import at.fhv.sysarch.lab1.obj.Face;
import at.fhv.sysarch.lab1.pipeline.Util;
import at.fhv.sysarch.lab1.pipeline.pipes.PullPipe;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class DepthSortingFilter implements PullFilter<Face, Face> {
    private final Queue<Face> processQueue;
    private boolean bufferingMode;
    private PullPipe<Face> inboundPipeline;

    public DepthSortingFilter() {
        bufferingMode = true;
        processQueue = new PriorityQueue<>((face1, face2) -> {
            // First face
            float firstZ1 = face1.getV1().getZ();
            float firstZ2 = face1.getV2().getZ();
            float firstZ3 = face1.getV3().getZ();

            float firstFaceAverageZ = (firstZ1 + firstZ2 + firstZ3) / 3;

            // Second face
            float secondZ1 = face2.getV1().getZ();
            float secondZ2 = face2.getV2().getZ();
            float secondZ3 = face2.getV3().getZ();

            float secondFaceAverageZ = (secondZ1 + secondZ2 + secondZ3) / 3;

            // Computed value is too small for rounding -> value * 100
            // value + 0.5 to round without using, for example, Math.round()
            return (int) ((firstFaceAverageZ - secondFaceAverageZ) * 100 + 0.5);
        });
    }

    @Override
    public Face read() {
        // When buffering mode active - pull all faces from previous filter
        while (bufferingMode) {
            Face input = inboundPipeline.read();
            if (null == input) {
                continue;
            }

            // Once end of stream is reached, end buffering mode
            if (Util.isFaceMakingEnd(input)) {
                bufferingMode = false;
            }

            processQueue.add(input);
        }

        // When buffer empty, start buffering mode
        if (processQueue.size() <= 1) {
            bufferingMode = true;
        }

        return this.processQueue.poll();
    }

    @Override
    public Face process(Face input) {
        // See read() method
        return input;
    }

    @Override
    public PullPipe<Face> getInboundPipeline() {
        return inboundPipeline;
    }

    @Override
    public void setInboundPipeline(PullPipe<Face> pipe) {
        this.inboundPipeline = pipe;
    }
}
