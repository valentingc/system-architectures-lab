package at.fhv.dluvgo.smarthome.actuators.mediastation.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;

public class MediaPlaybackStateRequestMessage implements Message {
    private final ActorRef<MediaPlaybackStateResponseMessage> replyTo;

    public MediaPlaybackStateRequestMessage(ActorRef<MediaPlaybackStateResponseMessage> replyTo) {
        this.replyTo = replyTo;
    }

    public ActorRef<MediaPlaybackStateResponseMessage> getReplyTo() {
        return replyTo;
    }
}
