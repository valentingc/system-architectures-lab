package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class AddProductMessage implements FridgeMessage {
    private final FridgeActor.Product product;

    public AddProductMessage(FridgeActor.Product product) {
        this.product = product;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }
}
