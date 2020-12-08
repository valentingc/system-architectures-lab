package at.fhv.dluvgo.smarthome;

import akka.actor.typed.ActorSystem;
import at.fhv.dluvgo.smarthome.messages.Message;
import at.fhv.dluvgo.smarthome.messages.InitEnvironmentMessage;

public class SmartHomeController {

    public static void main(String[] args) {
        final ActorSystem<Message> environment = ActorSystem.create(
            EnvironmentActor.create(),
            "SmartHome-Environment"
        );

        environment.tell(new InitEnvironmentMessage());
    }
}
