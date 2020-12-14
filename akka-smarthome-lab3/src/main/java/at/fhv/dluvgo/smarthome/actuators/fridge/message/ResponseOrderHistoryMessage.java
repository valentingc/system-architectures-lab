package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import java.util.List;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.OrderProcessorActor;

public class ResponseOrderHistoryMessage implements Message {
    private final List<OrderProcessorActor.OrderReceipt> orderHistory;

    public ResponseOrderHistoryMessage(List<OrderProcessorActor.OrderReceipt> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public List<OrderProcessorActor.OrderReceipt> getOrderHistory() {
        return this.orderHistory;
    }
}
