package at.fhv.dluvgo.smarthome.actuators.blinds;

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
import at.fhv.dluvgo.smarthome.actuators.blinds.message.WeatherChangedMessage;
import at.fhv.dluvgo.smarthome.common.WeatherType;
import at.fhv.dluvgo.smarthome.sensor.WeatherSensorActor;

public class BlindsActor extends AbstractBehavior<Message> {
    private boolean blindsClosed = false;
    private boolean mediaPlaybackRunning = false;
    private WeatherType currentWeather;

    public static Behavior<Message> create() {
        return Behaviors.setup(BlindsActor::new);
    }

    private BlindsActor(ActorContext<Message> context) {
        super(context);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(WeatherChangedMessage.class, this::onWeatherChanged)
            .onMessage(OpenBlindsRequestMessage.class, this::onOpenBlindsRequest)
            .onMessage(CloseBlindsRequestMessage.class, this::onCloseBlindsRequest)
            .onMessage(MediaPlaybackStartedMessage.class, this::onMediaPlaybackStarted)
            .onMessage(MediaPlaybackStoppedMessage.class, this::onMediaPlaybackStopped)
            .build();
    }

    /**
     * Handles changing weather ({@link WeatherChangedMessage}) measured by the
     * {@link WeatherSensorActor} by opening or closing the blinds accordingly.
     *
     * @param msg The weather change
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onWeatherChanged(
        WeatherChangedMessage msg
    ) {
        boolean previouslyClosed = this.blindsClosed;
        currentWeather = msg.getWeather();

        if (mediaPlaybackRunning) {
            this.blindsClosed = true;
        } else {
            this.blindsClosed = msg.getWeather().equals(WeatherType.SUNNY);
        }

        if (!previouslyClosed && this.blindsClosed) {
            getContext().getLog().info("The blinds are now: closed");
        } else if (previouslyClosed && !this.blindsClosed) {
            getContext().getLog().info("The blinds are now: open");
        }

        return this;
    }

    /**
     * Handles a request to open the blinds ({@link OpenBlindsRequestMessage}). If the current
     * weather is sunny ({@link WeatherType#SUNNY}), the blinds are kept closed.
     *
     * @param msg The request to open the blinds
     *
     * @return this - no change of behavior
     */
    private Behavior<Message> onOpenBlindsRequest(OpenBlindsRequestMessage msg) {
        if (currentWeather.equals(WeatherType.SUNNY)) {
            getContext().getLog().debug("Keeping blinds closed - weather is {}", WeatherType.SUNNY);
        } else {
            this.blindsClosed = true;
            getContext().getLog().info("The blinds are now: closed");
        }

        return this;
    }

    /**
     * Handles a request to close the blinds ({@link CloseBlindsRequestMessage}).
     *
     * @param msg The request to close the blinds
     *
     * @return this - no change of behavior
     */
    private Behavior<Message> onCloseBlindsRequest(CloseBlindsRequestMessage msg) {
        if (!blindsClosed) {
            getContext().getLog().info("The blinds are now: closed");
        }

        return this;
    }

    // TODO - JavaDoc
    private Behavior<Message> onMediaPlaybackStarted(MediaPlaybackStartedMessage msg) {
        this.mediaPlaybackRunning = true;
        return this;
    }

    // TODO - JavaDoc
    private Behavior<Message> onMediaPlaybackStopped(MediaPlaybackStoppedMessage msg) {
        this.mediaPlaybackRunning = false;
        return this;
    }
}
