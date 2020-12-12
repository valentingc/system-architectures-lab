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
    private final ActorRef<Message> replyTo;
    private final float MAX_WEIGHT;
    private final int MAX_ITEMS;

    public static final class OrderReceipt {
        private LocalDateTime orderDate;
        private List<FridgeActor.Product> productsOrdered;
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
    }

    public OrderProcessorActor(
        ActorContext<Message> context,
        ActorRef<Message> replyTo,
        float maxWeight,
        int maxItems
    ) {
        super(context);
        this.replyTo = replyTo;
        ;
        this.MAX_WEIGHT = maxWeight;
        this.MAX_ITEMS = maxItems;
    }

    public static Behavior<Message> create(
        ActorRef<Message> replyTo,
        float maxWeight,
        int maxItems
    ) {
        return Behaviors.setup(ctx -> new OrderProcessorActor(
            ctx,
            replyTo,
            maxWeight,
            maxItems
        ));
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

    private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
        FridgeActor.Product product = msg.getProductToOrder();
        List<FridgeActor.Product> products = msg.getCurrentProducts();

        getContext().getLog().info("Ordering Product: {}", msg.getProductToOrder().name);

        if ((products.size() + 1) > MAX_ITEMS) {
            // TODO - max items reached
            // TODO: return error msg
            getContext().getLog().info("Fridge is now full (max_items)");
            msg.getReplyTo().tell(
                new ProductOrderedUnsuccessfullyMessage(
                    product,
                    msg.getOriginalSender(),
                    "Max item count reached")
            );
            return Behaviors.same();
        } else if ((calculateTotalWeight(products) + product.weight) > MAX_WEIGHT) {
            // TODO - max weight reached
            getContext().getLog().info("Fridge is now full (max_weight)");
            msg.getReplyTo().tell(
                new ProductOrderedUnsuccessfullyMessage(
                    product,
                    msg.getOriginalSender(),
                    "Max weight reached"
                )
            );
            return Behaviors.same();
        }

        products.add(product);

        List<FridgeActor.Product> orderedProducts = new LinkedList<>();
        orderedProducts.add(product);
        msg.getReplyTo().tell(
            new ProductOrderedSuccessfullyMessage(
                msg.getProductToOrder(),
                msg.getOriginalSender(),
                new OrderReceipt(orderedProducts)
            )
        );
        return Behaviors.same();
    }
}
