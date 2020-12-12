package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class ProductOrderedSuccessfullyMessage implements Message {
    private final FridgeActor.Product product;

    public ProductOrderedSuccessfullyMessage(FridgeActor.Product product) {
        this.product = product;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

}

