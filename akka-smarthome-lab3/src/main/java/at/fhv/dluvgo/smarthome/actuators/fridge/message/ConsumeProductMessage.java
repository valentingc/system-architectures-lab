package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;

public class ConsumeProductMessage implements Message {
    private final FridgeActor.Product product;
    private final ActorRef<Message> replyTo;

    public ConsumeProductMessage(FridgeActor.Product product, ActorRef<Message> replyTo) {
        this.product = product;
        this.replyTo = replyTo;
    }

    public FridgeActor.Product getProduct() {
        return product;
    }

    public ActorRef<Message> getReplyTo() {
        return replyTo;
    }
}
