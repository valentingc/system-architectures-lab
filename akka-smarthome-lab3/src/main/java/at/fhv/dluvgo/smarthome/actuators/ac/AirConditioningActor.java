package at.fhv.dluvgo.smarthome.actuators.ac;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.ac.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.sensor.TemperatureSensorActor;

public class AirConditioningActor extends AbstractBehavior<TemperatureChangedMessage> {
    private boolean acEnabled = false;

    public static Behavior<TemperatureChangedMessage> create() {
        return Behaviors.setup(AirConditioningActor::new);
    }

    private AirConditioningActor(ActorContext<TemperatureChangedMessage> context) {
        super(context);
    }

    @Override
    public Receive<TemperatureChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(TemperatureChangedMessage.class, this::onTemperatureChanged)
            .build();
    }

    /**
     * Handles changing temperatures ({@link TemperatureChangedMessage}) measured by the
     * {@link TemperatureSensorActor} by activating or deactivating the AC accordingly.
     *
     * @param msg The temperature change
     *
     * @return this - no change of behaviour
     */
    private Behavior<TemperatureChangedMessage> onTemperatureChanged(
        TemperatureChangedMessage msg
    ) {
        boolean previouslyEnabled = acEnabled;
        acEnabled = msg.getTemperature().getValue() >= 20.0f;

        if (!previouslyEnabled && acEnabled) {
            getContext().getLog().info("The air conditioning now is: active");
        } else if (previouslyEnabled && !acEnabled) {
            getContext().getLog().info("The air conditioning now is: inactive");
        }

        return this;
    }
}
