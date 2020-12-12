package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import java.util.List;

public class OrderProductMessage implements Message {
    private final FridgeActor.Product productToOrder;
    private final ActorRef<Message> originalSender;
    private final ActorRef<Message> replyTo;
    private final List<FridgeActor.Product> currentProducts;

    public OrderProductMessage(
        FridgeActor.Product product,
        ActorRef<Message> originalSender,
        ActorRef<Message> replyTo,
        List<FridgeActor.Product> currentProducts
    ) {
        this.productToOrder = product;
        this.originalSender = originalSender;
        this.replyTo = replyTo;
        this.currentProducts = currentProducts;
    }

    public FridgeActor.Product getProductToOrder() {
        return productToOrder;
    }

    public ActorRef<Message> getOriginalSender() {
        return originalSender;
    }

    public ActorRef<Message> getReplyTo() {
        return replyTo;
    }

    public List<FridgeActor.Product> getCurrentProducts() {
        return currentProducts;
    }
}

