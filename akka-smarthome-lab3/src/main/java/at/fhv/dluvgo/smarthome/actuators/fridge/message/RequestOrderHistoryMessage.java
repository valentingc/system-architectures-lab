package at.fhv.dluvgo.smarthome.actuators.fridge.message;

import akka.actor.typed.ActorRef;
import at.fhv.dluvgo.smarthome.Message;

public class RequestOrderHistoryMessage implements Message {
    private final ActorRef<Message> replyTo;

    public RequestOrderHistoryMessage(ActorRef<Message> replyTo) {
        this.replyTo = replyTo;
    }

    public ActorRef<Message> getReplyTo() {
        return this.replyTo;
    }
}
