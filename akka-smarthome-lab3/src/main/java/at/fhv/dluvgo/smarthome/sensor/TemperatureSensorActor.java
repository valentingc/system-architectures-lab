package at.fhv.dluvgo.smarthome.sensor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.AirConditioningActor;
import at.fhv.dluvgo.smarthome.actuators.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.common.Temperature;
import at.fhv.dluvgo.smarthome.sensor.message.EnvTemperatureChangedMessage;

public class TemperatureSensorActor extends AbstractBehavior<EnvTemperatureChangedMessage> {
    private final ActorRef<TemperatureChangedMessage> airConditioning;
    private Temperature temperature;

    public static Behavior<EnvTemperatureChangedMessage> create(
        ActorRef<TemperatureChangedMessage> airConditioning
    ) {
        return Behaviors.setup(ctx -> new TemperatureSensorActor(
            ctx,
            airConditioning
        ));
    }

    private TemperatureSensorActor(
        ActorContext<EnvTemperatureChangedMessage> context,
        ActorRef<TemperatureChangedMessage> airConditioning
    ) {
        super(context);
        this.airConditioning = airConditioning;
    }

    @Override
    public Receive<EnvTemperatureChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(EnvTemperatureChangedMessage.class, this::onEnvTemperatureChanged)
            .build();
    }

    /**
     * Handles changing environment temperatures ({@link EnvTemperatureChangedMessage}) by
     * notifying the {@link AirConditioningActor} about changed {@link Temperature} conditions.
     *
     * @param msg The new environment temperature
     *
     * @return this - no change of behavior
     */
    private Behavior<EnvTemperatureChangedMessage> onEnvTemperatureChanged(
        EnvTemperatureChangedMessage msg
    ) {
        temperature = new Temperature(Temperature.Unit.CELSIUS, msg.getTemperature());
        getContext().getLog().info("Received new temperature values");

        airConditioning.tell(new TemperatureChangedMessage(temperature));
        return this;
    }
}
