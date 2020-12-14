package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.OrderProcessorActor;

public class ProductOrderedSuccessfullyMessage implements Message {
    private final FridgeActor.Product product;
    private final OrderProcessorActor.OrderReceipt receipt;

    public ProductOrderedSuccessfullyMessage(
        FridgeActor.Product product,
        OrderProcessorActor.OrderReceipt receipt
    ) {
        this.product = product;
        this.receipt = receipt;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

    public OrderProcessorActor.OrderReceipt getReceipt() {
        return this.receipt;
    }

}

