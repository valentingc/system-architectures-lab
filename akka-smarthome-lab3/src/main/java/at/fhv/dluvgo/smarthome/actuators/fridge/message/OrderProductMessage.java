package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import java.util.List;

public class OrderProductMessage implements Message {
    private final FridgeActor.Product productToOrder;
    private final ActorRef<Message> replyTo;

    public OrderProductMessage(
        FridgeActor.Product product,
        ActorRef<Message> replyTo
    ) {
        this.productToOrder = product;
        this.replyTo = replyTo;
    }

    public FridgeActor.Product getProductToOrder() {
        return productToOrder;
    }

    public ActorRef<Message> getReplyTo() {
        return replyTo;
    }

}

