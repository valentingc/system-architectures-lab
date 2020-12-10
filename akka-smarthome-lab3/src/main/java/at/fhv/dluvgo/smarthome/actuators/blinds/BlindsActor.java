package at.fhv.dluvgo.smarthome.actuators.blinds;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.ac.AirConditioningActor;
import at.fhv.dluvgo.smarthome.actuators.ac.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.message.WeatherChangedMessage;
import at.fhv.dluvgo.smarthome.common.WeatherType;
import at.fhv.dluvgo.smarthome.sensor.WeatherSensorActor;

public class BlindsActor extends AbstractBehavior<WeatherChangedMessage> {

    private boolean blindsClosed;

    private BlindsActor(ActorContext<WeatherChangedMessage> context) {
        super(context);
    }

    public static Behavior<WeatherChangedMessage> create() {
        return Behaviors.setup(BlindsActor::new);
    }

    @Override
    public Receive<WeatherChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(WeatherChangedMessage.class, this::onWeatherChanged)
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
    private Behavior<WeatherChangedMessage> onWeatherChanged(
        WeatherChangedMessage msg
    ) {
        boolean previouslyClosed = this.blindsClosed;

        this.blindsClosed = msg.getWeather().equals(WeatherType.SUNNY);

        if (!previouslyClosed && this.blindsClosed) {
            getContext().getLog().info("The blinds are now: closed");
        } else if (previouslyClosed && !this.blindsClosed) {
            getContext().getLog().info("The blinds are now: open");
        }

        return this;
    }

}
