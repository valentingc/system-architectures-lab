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
import java.util.LinkedList;
import java.util.List;

public class OrderProcessorActor extends AbstractBehavior<Message> {
    private final ActorRef<Message> replyTo;
    private final float MAX_WEIGHT;
    private final int MAX_ITEMS;

    public OrderProcessorActor(
        ActorContext<Message> context,
        ActorRef<Message> replyTo,
        float maxWeight,
        int maxItems
    ) {
        super(context);
        this.replyTo = replyTo;;
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
            getContext().getLog().info("Fridge is now full (max_items)");
            return FridgeActor.FullFridgeBehavior.create(products);
        } else if ((calculateTotalWeight(products) + product.weight) > MAX_WEIGHT) {
            // TODO - max weight reached
            getContext().getLog().info("Fridge is now full (max_weight)");
            return FridgeActor.FullFridgeBehavior.create(products);
        }

        products.add(product);
        msg.getReplyTo().tell(new ProductOrderedSuccessfullyMessage(msg.getProductToOrder()));
        return Behaviors.same();
    }
}
