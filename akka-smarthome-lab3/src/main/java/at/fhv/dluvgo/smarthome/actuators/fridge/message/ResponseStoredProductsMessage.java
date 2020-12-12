package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import java.util.List;

public class ResponseStoredProductsMessage implements Message {
    public final List<FridgeActor.Product> products;

    public ResponseStoredProductsMessage(List<FridgeActor.Product> products) {
        this.products = products;
    }

    public List<FridgeActor.Product> getProducts() {
        return products;
    }
}
