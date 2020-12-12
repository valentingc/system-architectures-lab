package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class ProductOrderedSuccessfullyMessage implements FridgeMessage {
    private final FridgeActor.Product product;

    public ProductOrderedSuccessfullyMessage(FridgeActor.Product product) {
        this.product = product;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

}

