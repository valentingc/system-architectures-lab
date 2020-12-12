package at.fhv.dluvgo.smarthome.actuators.fridge;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ConsumeProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.FridgeMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ProductOrderedSuccessfullyMessage;

public class OrderProcessorActor extends AbstractBehavior<Message> {
    ActorRef<FridgeMessage> replyTo;

    public static Behavior<Message> create(ActorRef<FridgeMessage> replyTo) {
        return Behaviors.setup(ctx -> new OrderProcessorActor(ctx, replyTo));
    }

    public OrderProcessorActor(ActorContext<Message> context, ActorRef<FridgeMessage> replyTo) {
        super(context);
        this.replyTo = replyTo;
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(OrderProductMessage.class, this::onOrderProduct)
            .build();
    }

    private Behavior<Message> onOrderProduct(OrderProductMessage msg) {
        getContext().getLog().info("Ordering Product: {}", msg.getProduct().name);
        msg.getReplyTo().tell(new ProductOrderedSuccessfullyMessage(msg.getProduct()));
        return Behaviors.same();
    }
}
