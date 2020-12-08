package at.fhv.dluvgo.smarthome.sensor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.common.WeatherType;
import at.fhv.dluvgo.smarthome.sensor.message.EnvWeatherChangedMessage;

public class WeatherSensorActor extends AbstractBehavior<EnvWeatherChangedMessage> {
    private WeatherType weather;

    public static Behavior<EnvWeatherChangedMessage> create() {
        return Behaviors.setup(WeatherSensorActor::new);
    }

    public WeatherSensorActor(ActorContext<EnvWeatherChangedMessage> context) {
        super(context);
    }

    @Override
    public Receive<EnvWeatherChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(EnvWeatherChangedMessage.class, this::onEnvWeatherChanged)
            .build();
    }

    private Behavior<EnvWeatherChangedMessage> onEnvWeatherChanged(EnvWeatherChangedMessage msg) {
        weather = WeatherType.values()[msg.getWeather()];
        getContext().getLog().info("Received new weather value");

        return this;
    }
}
