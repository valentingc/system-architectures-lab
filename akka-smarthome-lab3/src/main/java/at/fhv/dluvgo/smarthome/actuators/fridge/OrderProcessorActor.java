package at.fhv.dluvgo.smarthome.actuators.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedSuccessfullyMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedUnsuccessfullyMessage;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class OrderProcessorActor extends AbstractBehavior<Message> {
    public static final class OrderReceipt {
        private final LocalDateTime orderDate;
        private final List<FridgeActor.Product> productsOrdered;
        private double orderSum;

        public OrderReceipt(List<FridgeActor.Product> productsOrdered) {
            this.orderDate = LocalDateTime.now();
            this.productsOrdered = productsOrdered;
            this.calculateOrderSum();
        }

        private void calculateOrderSum() {
            double orderSum = 0.0d;
            for (FridgeActor.Product p : this.productsOrdered) {
                orderSum += p.price;
            }
            this.orderSum = orderSum;
        }

        @Override
        public String toString() {
            return "Order date: " + orderDate + "; "
                + "Number of products: #" + productsOrdered.size() + ";"
                + "Sum: " + orderSum + "€";
        }
    }

    public static Behavior<Message> create(ActorRef<Message> replyTo) {
        return Behaviors.setup(OrderProcessorActor::new);
    }

    private OrderProcessorActor(ActorContext<Message> context) {
        super(context);
    }

    private float calculateTotalWeight(List<FridgeActor.Product> currentProducts) {
        float weight = 0.0f;
        for (FridgeActor.Product product : currentProducts) {
            weight += product.weight;
        }

        return weight;
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(OrderProductMessage.class, this::onOrderProduct)
            .build();
    }

    /**
     * Handles a product order {@link OrderProductMessage}. If the fridge is full (either by
     * {@link FridgeActor#MAX_ITEMS} or by {@link FridgeActor#MAX_WEIGHT}), the order is ignored.
     *
     * @param msg The order message
     *
     * @return {@link Behaviors#same()} - no change of behavior
     */
    private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
        FridgeActor.Product product = msg.getProductToOrder();
        List<FridgeActor.Product> products = msg.getCurrentProducts();

        getContext().getLog().info("Ordering product: {}", msg.getProductToOrder().name);

        if ((products.size() + 1) > FridgeActor.MAX_ITEMS) {
            getContext().getLog().info("Fridge is now full (maximum amount of items reached)");
            msg.getReplyTo().tell(new ProductOrderedUnsuccessfullyMessage(
                product,
                msg.getOriginalSender(),
                "Max item count reached"
            ));
            return Behaviors.same();
        } else if ((calculateTotalWeight(products) + product.weight) > FridgeActor.MAX_WEIGHT) {
            getContext().getLog().info("Fridge is now full (maximum weight reached)");
            msg.getReplyTo().tell(new ProductOrderedUnsuccessfullyMessage(
                product,
                msg.getOriginalSender(),
                "Max weight reached"
            ));
            return Behaviors.same();
        }

        products.add(product);

        List<FridgeActor.Product> orderedProducts = new LinkedList<>();
        orderedProducts.add(product);

        msg.getReplyTo().tell(new ProductOrderedSuccessfullyMessage(
            msg.getProductToOrder(),
            msg.getOriginalSender(),
            new OrderReceipt(orderedProducts)
        ));
        return Behaviors.same();
    }
}
