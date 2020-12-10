package at.fhv.dluvgo.smarthome.actuators.mediastation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.blinds.message.CloseBlindsRequestMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.message.MediaPlaybackStartedMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.message.MediaPlaybackStoppedMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.message.OpenBlindsRequestMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.MediaPlaybackRequestMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.MediaPlaybackStateRequestMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.MediaPlaybackStateResponseMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.StopMediaPlaybackRequestMessage;

public class MediaStationActor extends AbstractBehavior<Message> {
    private final ActorRef<Message> blinds;
    private boolean mediaPlaying = false;
    private String currentMovie = null;

    public static Behavior<Message> create(ActorRef<Message> blinds) {
        return Behaviors.setup(ctx -> new MediaStationActor(ctx, blinds));
    }

    private MediaStationActor(
        ActorContext<Message> context,
        ActorRef<Message> blinds
    ) {
        super(context);
        this.blinds = blinds;
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(MediaPlaybackStateRequestMessage.class, this::onMediaPlaybackStateRequest)
            .onMessage(MediaPlaybackRequestMessage.class, this::onMediaPlaybackRequest)
            .onMessage(StopMediaPlaybackRequestMessage.class, this::onStopMediaPlaybackRequest)
            .build();
    }

    /**
     * Handles a playback state request ({@link MediaPlaybackStateRequestMessage}) and provides
     * the requested information accordingly ({@link MediaPlaybackStateResponseMessage}).
     *
     * @param msg The playback state request
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onMediaPlaybackStateRequest(MediaPlaybackStateRequestMessage msg) {
        msg.getReplyTo().tell(new MediaPlaybackStateResponseMessage(mediaPlaying));
        return this;
    }

    /**
     * Handles the request to start a media playback ({@link MediaPlaybackRequestMessage})
     * should there be no other active playback. Tells the blinds to close if playback starts.
     *
     * @param msg The request to start a playback
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onMediaPlaybackRequest(MediaPlaybackRequestMessage msg) {
        if (mediaPlaying) {
            getContext().getLog().info(
                "Cannot play movie [{}], there currently is an active media playback of [{}]",
                msg.getMovieTitle(),
                currentMovie
            );
        } else {
            mediaPlaying = true;
            currentMovie = msg.getMovieTitle();

            getContext().getLog().info(
                "Starting media playback of [{}]",
                msg.getMovieTitle()
            );

            blinds.tell(new MediaPlaybackStartedMessage());
            blinds.tell(new CloseBlindsRequestMessage());
        }

        return this;
    }

    /**
     * Handles the request to stop the current media playback
     * ({@link StopMediaPlaybackRequestMessage}) should a media playback be active right now.
     * Tells the blinds to open if playback stops.
     *
     * @param msg The request to stop the playback
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onStopMediaPlaybackRequest(StopMediaPlaybackRequestMessage msg) {
        if (mediaPlaying) {
            mediaPlaying = false;
            currentMovie = null;

            getContext().getLog().info(
                "Stopping media playback of [{}]",
                currentMovie
            );

            blinds.tell(new MediaPlaybackStoppedMessage());
            blinds.tell(new OpenBlindsRequestMessage());
        } else {
            getContext().getLog().info(
                "Cannot stop media playback, there currently is no active playback"
            );
        }

        return this;
    }
}
