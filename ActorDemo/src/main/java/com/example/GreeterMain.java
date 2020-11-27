package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * @author Valentin
 */
public class GreeterMain extends AbstractBehavior<GreeterMain.SayHello> {

    private final ActorRef<Greeter.Greet> greeter;

    public static final class SayHello {
        public final String name;

        public SayHello(String name) {
            this.name = name;
        }
    }

    private GreeterMain(ActorContext<SayHello> context) {
        super(context);
        this.greeter = context.spawn(Greeter.create(), "Greeter");
    }

    public static Behavior<SayHello> create() {
        return Behaviors.setup(GreeterMain::new);
    }

    @Override
    public Receive<SayHello> createReceive() {
        return newReceiveBuilder().onMessage(SayHello.class, this::onSayHello).build();
    }

    private Behavior<SayHello> onSayHello(SayHello cmd) {
        ActorRef<Greeter.Greeted> greeterBot = getContext().spawn(GreeterBot.create(3), "Greeter Bot");
        this.greeter.tell(new Greeter.Greet(cmd.name, greeterBot));

        return this;
    }

}
