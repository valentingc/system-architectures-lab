package at.fhv.dluvgo.smarthome.actuators;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.message.TemperatureChangedMessage;

public class AirConditioningActor extends AbstractBehavior<TemperatureChangedMessage> {
    private boolean isAcEnabled = false;

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


    private Behavior<TemperatureChangedMessage> onTemperatureChanged(
        TemperatureChangedMessage msg
    ) {
        this.isAcEnabled = msg.getTemperature().getValue() >= 20.0f;
        getContext().getLog().info(
            "The air conditioning now is: {}",
            this.isAcEnabled ? "active" : "inactive"
        );

        return this;
    }
}
