package com.example;

import akka.actor.Actor;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * @author Valentin
 */
public class GreeterBot extends AbstractBehavior<Greeter.Greeted> {

    private final int max;
    private int greetingCounter;

    public static Behavior<Greeter.Greeted> create(int max) {
        return Behaviors.setup(context -> new GreeterBot(context, max));
    }
    private GreeterBot(ActorContext<Greeter.Greeted> context, int max) {
        super(context);
        this.max = max;
    }
    @Override
    public Receive<Greeter.Greeted> createReceive() {
        return newReceiveBuilder().onMessage(Greeter.Greeted.class, this::onGreeted).build();
    }

    private Behavior<Greeter.Greeted> onGreeted(Greeter.Greeted cmd) {
        this.greetingCounter++;

        getContext().getLog().info("Greeting {} for {}!", greetingCounter, cmd.whom);

        if (this.greetingCounter == max) {
            return Behaviors.stopped();
        } else {
            cmd.replyTo.tell(new Greeter.Greet(cmd.whom, getContext().getSelf()));
        }

        return this;
    }
}
