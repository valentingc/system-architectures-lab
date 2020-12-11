package at.fhv.dluvgo.smarthome.actuators.cli;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ResponseStoredProductsMessage;

public class UserInputActor extends AbstractBehavior<Message> {

    public UserInputActor(
        ActorContext<Message> context) {
        super(context);
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(UserInputActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(ResponseStoredProductsMessage.class, this::onStoredProductsResponse)
            .build();
    }

    private Behavior<Message> onStoredProductsResponse(ResponseStoredProductsMessage msg) {
        getContext().getLog().info("Received response from getStoredProducts (fridge)");

        for (FridgeActor.Product p : msg.getProducts()) {
            getContext().getLog().info("Fridge contains: {}", p.name);
        }

        return Behaviors.same();
    }
}
