package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class ProductOrderedUnsuccessfullyMessage implements Message {
    private final FridgeActor.Product product;
    private final String reason;

    public ProductOrderedUnsuccessfullyMessage(
        FridgeActor.Product product,
        String reason
    ) {
        this.product = product;
        this.reason = reason;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

    public String getReason() {
        return this.reason;
    }
}
