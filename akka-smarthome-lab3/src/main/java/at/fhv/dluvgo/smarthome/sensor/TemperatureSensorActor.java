package at.fhv.dluvgo.smarthome.sensor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.common.Temperature;
import at.fhv.dluvgo.smarthome.sensor.message.TemperatureChangedMessage;

public class TemperatureSensorActor extends AbstractBehavior<TemperatureChangedMessage> {
    private Temperature temperature;

    public static Behavior<TemperatureChangedMessage> create() {
        return Behaviors.setup(TemperatureSensorActor::new);
    }

    private TemperatureSensorActor(ActorContext<TemperatureChangedMessage> context) {
        super(context);
    }

    @Override
    public Receive<TemperatureChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(TemperatureChangedMessage.class, this::onTemperatureChanged)
            .build();
    }

    private Behavior<TemperatureChangedMessage> onTemperatureChanged(
        TemperatureChangedMessage msg
    ) {
        temperature = new Temperature(Temperature.Unit.CELSIUS, msg.getTemperature());
        getContext().getLog().info("Received new temperature values");

        return this;
    }
}
