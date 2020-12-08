package com.example;

import akka.actor.typed.ActorSystem;
import com.example.messages.Command;
import com.example.messages.TemperatureChangeRequest;


public class SmartHomeController {
    public SmartHomeController() {

    }

    public static void main(String[] args) {

        final akka.actor.typed.ActorSystem<Command> environment =
            ActorSystem.create(EnvironmentActor.create(), "environment");

        environment.tell(new TemperatureChangeRequest());
    }
}
