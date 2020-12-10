package at.fhv.dluvgo.smarthome.sensor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.ac.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.message.WeatherChangedMessage;
import at.fhv.dluvgo.smarthome.common.WeatherType;
import at.fhv.dluvgo.smarthome.sensor.message.EnvWeatherChangedMessage;

public class WeatherSensorActor extends AbstractBehavior<EnvWeatherChangedMessage> {
    private final ActorRef<Message> blinds;
    private WeatherType weather;

    public static Behavior<EnvWeatherChangedMessage> create(
        ActorRef<Message> blinds
    ) {
        return Behaviors.setup(ctx -> new WeatherSensorActor(ctx, blinds));
    }

    private WeatherSensorActor(
        ActorContext<EnvWeatherChangedMessage> context,
        ActorRef<Message> blinds
    ) {
        super(context);
        this.blinds = blinds;
    }

    @Override
    public Receive<EnvWeatherChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(EnvWeatherChangedMessage.class, this::onEnvWeatherChanged)
            .build();
    }

    // TODO - JavaDoc once blinds and media station have been added
    private Behavior<EnvWeatherChangedMessage> onEnvWeatherChanged(EnvWeatherChangedMessage msg) {
        weather = WeatherType.values()[msg.getWeather()];
        getContext().getLog().debug("Received new weather value");

        blinds.tell(new WeatherChangedMessage(weather));
        return this;
    }
}
