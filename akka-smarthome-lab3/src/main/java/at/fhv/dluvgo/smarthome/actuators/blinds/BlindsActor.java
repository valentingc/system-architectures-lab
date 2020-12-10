package at.fhv.dluvgo.smarthome.actuators.blinds;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.ac.message.TemperatureChangedMessage;

public class BlindsActor extends AbstractBehavior<TemperatureChangedMessage> {
    public BlindsActor(ActorContext<TemperatureChangedMessage> context) {
        super(context);
    }

    @Override
    public Receive<TemperatureChangedMessage> createReceive() {
        return null;
    }
}
