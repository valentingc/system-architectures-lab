package at.fhv.dluvgo.smarthome.actuators.mediastation.message;

import at.fhv.dluvgo.smarthome.Message;

public class MediaPlaybackStateResponseMessage implements Message {
    private final boolean isPlaybackRunning;

    public MediaPlaybackStateResponseMessage(boolean isPlaybackRunning) {
        this.isPlaybackRunning = isPlaybackRunning;
    }

    public boolean isPlaybackRunning() {
        return isPlaybackRunning;
    }
}
