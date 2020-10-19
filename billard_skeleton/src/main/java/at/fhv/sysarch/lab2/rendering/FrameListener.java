package at.fhv.sysarch.lab2.rendering;

@FunctionalInterface
public interface FrameListener {
    public void onFrame(double dt);
}