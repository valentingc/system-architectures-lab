package at.fhv.dluvgo.smarthome.actuators.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.FridgeMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedSuccessfullyMessage;
import java.util.LinkedList;
import java.util.List;

public class OrderProcessorActor extends AbstractBehavior<FridgeMessage> {
    private final ActorRef<FridgeMessage> replyTo;
    private final List<FridgeActor.Product> products = new LinkedList<>();
    private final float MAX_WEIGHT;
    private final int MAX_ITEMS;

    public OrderProcessorActor(
        ActorContext<FridgeMessage> context,
        ActorRef<FridgeMessage> replyTo,
        List<FridgeActor.Product> products,
        float maxWeight,
        int maxItems
    ) {
        super(context);
        this.replyTo = replyTo;
        this.products.addAll(products);
        this.MAX_WEIGHT = maxWeight;
        this.MAX_ITEMS = maxItems;
    }

    public static Behavior<FridgeMessage> create(
        ActorRef<FridgeMessage> replyTo,
        List<FridgeActor.Product> products,
        float maxWeight,
        int maxItems
    ) {
        return Behaviors.setup(ctx -> new OrderProcessorActor(
            ctx,
            replyTo,
            products,
            maxWeight,
            maxItems
        ));
    }

    private float calculateTotalWeight() {
        float weight = 0.0f;
        for (FridgeActor.Product product : products) {
            weight += product.weight;
        }

        return weight;
    }

    @Override
    public Receive<FridgeMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(OrderProductMessage.class, this::onOrderProduct)
            .build();
    }

    private Behavior<FridgeMessage> onOrderProduct(OrderProductMessage msg) {
        FridgeActor.Product product = msg.getProduct();

        getContext().getLog().info("Ordering Product: {}", msg.getProduct().name);

        if ((products.size() + 1) > MAX_ITEMS) {
            // TODO - max items reached
            getContext().getLog().info("Fridge is now full (max_items)");
            return FridgeActor.FullFridgeBehavior.create(products);
        } else if ((calculateTotalWeight() + product.weight) > MAX_WEIGHT) {
            // TODO - max weight reached
            getContext().getLog().info("Fridge is now full (max_weight)");
            return FridgeActor.FullFridgeBehavior.create(products);
        }

        products.add(product);
        msg.getReplyTo().tell(new ProductOrderedSuccessfullyMessage(msg.getProduct()));
        return Behaviors.same();
    }
}
