package com.example;

import akka.actor.typed.ActorSystem;
import java.io.IOException;

/**
 * @author Valentin
 */
public class AkkaHelloWorld {
    public static void main(String[] args) {
        ActorSystem<GreeterMain.SayHello> actorSystem = ActorSystem.create(
            GreeterMain.create(),
            "HelloAkka"
        );

        actorSystem.tell(new GreeterMain.SayHello("Valentin"));

        try {
            System.out.println("---> Press ENTER to exit");
            System.in.read();
        } catch (IOException ignored) {
            System.err.println(ignored);
            actorSystem.terminate();
        }
    }
}
