package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;

public class RequestStoredProductsMessage implements FridgeMessage {
    public final ActorRef<FridgeMessage> replyTo;

    public RequestStoredProductsMessage(ActorRef<FridgeMessage> replyTo) {
        this.replyTo = replyTo;
    }

}
