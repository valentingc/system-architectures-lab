package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;

public class RequestStoredProductsMessage implements Message {
    public final ActorRef<Message> replyTo;

    public RequestStoredProductsMessage(ActorRef<Message> replyTo) {
        this.replyTo = replyTo;
    }

}
