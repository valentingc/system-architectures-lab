package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import java.util.List;

public class ResponseStoredProductsMessage implements FridgeMessage {
    public final List<FridgeActor.Product> products;

    public ResponseStoredProductsMessage(List<FridgeActor.Product> products) {
        this.products = products;
    }

    public List<FridgeActor.Product> getProducts() {
        return products;
    }
}
