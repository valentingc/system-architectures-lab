package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class ProductOrderedUnsuccessfullyMessage implements Message {
    private final FridgeActor.Product product;
    private final ActorRef<Message> originalSender;
    private final String reason;

    public ProductOrderedUnsuccessfullyMessage(
        FridgeActor.Product product,
        ActorRef<Message> originalSender,
        String reason
    ) {
        this.product = product;
        this.originalSender = originalSender;
        this.reason = reason;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

    public ActorRef<Message> getOriginalSender() {
        return originalSender;
    }

    public String getReason() {
        return this.reason;
    }
}
