package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;

/**
 * @author Valentin
 */
public class Greeter extends AbstractBehavior<Greeter.Greet> {

    public static Behavior<Greet> create() {
        return Behaviors.setup(ctx -> new Greeter(ctx));
    }

    private Greeter(ActorContext<Greet> context) {
        super(context);
    }

    @Override
    public Receive<Greet> createReceive() {
        // messagehandler initialisieren
        // wie wir auf welche klasse / auf welche message reagieren
        return newReceiveBuilder().onMessage(Greet.class, this::onGreet).build();
    }

    private Behavior<Greet> onGreet(Greet cmd) {
        getContext().getLog().info("Hello {}!", cmd.whom);

        cmd.replyTo.tell(new Greeted(cmd.whom, getContext().getSelf()));

        return this;
    }

    public static final class Greet {

        public final String whom;
        public final ActorRef<Greeted> replyTo;

        public Greet(String whom, ActorRef<Greeted> replyto) {
            this.whom = whom;
            this.replyTo = replyto;
        }
    }

    public static final class Greeted {

        public final String whom;
        public final ActorRef<Greet> replyTo;

        public Greeted(String whom, ActorRef<Greet> replyto) {
            this.whom = whom;
            this.replyTo = replyto;
        }
    }
}
