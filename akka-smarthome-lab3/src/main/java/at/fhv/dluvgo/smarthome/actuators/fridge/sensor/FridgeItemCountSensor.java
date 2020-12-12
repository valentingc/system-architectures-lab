package at.fhv.dluvgo.smarthome.actuators.fridge.sensor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;

public class FridgeItemCountSensor extends AbstractBehavior<ItemCountChangedMessage> {
    private int itemCount;

    public static Behavior<ItemCountChangedMessage> create(
    ) {
        return Behaviors.setup(ctx -> new FridgeItemCountSensor(
            ctx
        ));
    }

    private FridgeItemCountSensor(
        ActorContext<ItemCountChangedMessage> context
    ) {
        super(context);
    }

    @Override
    public Receive<ItemCountChangedMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(ItemCountChangedMessage.class, this::onItemCountChanged)
            .build();
    }

    private Behavior<ItemCountChangedMessage> onItemCountChanged(
        ItemCountChangedMessage msg
    ) {
        itemCount = msg.getItemCount();
        getContext().getLog().debug("Received new fridge item count value: {}", msg.getItemCount());

        return this;
    }
}