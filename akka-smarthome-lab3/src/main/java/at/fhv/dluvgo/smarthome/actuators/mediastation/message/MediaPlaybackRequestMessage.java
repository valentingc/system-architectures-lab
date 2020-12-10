package at.fhv.dluvgo.smarthome.actuators.mediastation.message;

import at.fhv.dluvgo.smarthome.Message;

public class MediaPlaybackRequestMessage implements Message {
    private final String movieTitle;

    public MediaPlaybackRequestMessage(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
}
