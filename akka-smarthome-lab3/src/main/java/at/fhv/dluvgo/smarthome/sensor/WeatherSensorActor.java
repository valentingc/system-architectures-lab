package at.fhv.dluvgo.smarthome.sensor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.common.WeatherType;
import at.fhv.dluvgo.smarthome.sensor.message.WeatherChangedMessage;

public class WeatherSensorActor extends AbstractBehavior<WeatherChangedMessage> {
    private WeatherType weather;

    public static Behavior<WeatherChangedMessage> create() {
        return Behaviors.setup(WeatherSensorActor::new);
    }

    public WeatherSensorActor(ActorContext<WeatherChangedMessage> context) {
        super(context);
    }

    @Override
    public Receive<WeatherChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(WeatherChangedMessage.class, this::onWeatherChanged)
            .build();
    }

    private Behavior<WeatherChangedMessage> onWeatherChanged(WeatherChangedMessage msg) {
        weather = WeatherType.values()[msg.getWeather()];
        getContext().getLog().info("Received new weather value");

        return this;
    }
}
