package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class OrderProductMessage implements FridgeMessage {
    private final FridgeActor.Product product;
    private final ActorRef<FridgeMessage> replyTo;

    public OrderProductMessage(FridgeActor.Product product, ActorRef<FridgeMessage> replyTo) {
        this.product = product;
        this.replyTo = replyTo;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

    public ActorRef<FridgeMessage> getReplyTo() {
        return replyTo;
    }
}

