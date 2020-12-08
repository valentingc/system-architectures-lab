package com.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import com.example.messages.Command;
import com.example.messages.TemperatureChangeRequest;
import java.time.Duration;

/**
 * @author Valentin
 */
public class EnvironmentActor extends AbstractBehavior<Command> {

    private float currentTemperature = 22.0f;
    private final TimerScheduler<Command> timers;
    private static final Object TIMER_KEY = new Object();

    private enum Timeout implements Command {
        INSTANCE
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(ctx -> Behaviors.withTimers(timers -> new EnvironmentActor(
            ctx,
            timers
        )));
    }

    public EnvironmentActor(
        ActorContext<Command> context,
        TimerScheduler<Command> timers
    ) {
        super(context);
        this.timers = timers;
    }


    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(Timeout.class, message -> onTimeout())
            .onMessage(Command.class, this::onCommand)
            .build();
    }

    private Behavior<Command> onCommand(Command msg) {
        System.out.println("onCommand");
        Duration delay = Duration.ofSeconds(1);
        //timers.startSingleTimer(TIMER_KEY, Timeout.INSTANCE, delay);
        timers.startTimerAtFixedRate(TIMER_KEY, new TemperatureChangeRequest(), delay);
        return this;
    }

    private Behavior<Command> onTimeout() {
        System.out.println("onTimeout.");
        // handle timeout message
        return this;
    }

}
