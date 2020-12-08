package at.fhv.dluvgo.smarthome;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.LogOptions;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import at.fhv.dluvgo.smarthome.environment.EnvironmentActor;
import at.fhv.dluvgo.smarthome.environment.message.InitEnvironmentMessage;
import at.fhv.dluvgo.smarthome.sensor.TemperatureSensorActor;
import at.fhv.dluvgo.smarthome.sensor.WeatherSensorActor;
import at.fhv.dluvgo.smarthome.sensor.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.sensor.message.WeatherChangedMessage;

public class SmartHomeController {

    public static Behavior<Void> create() {
        return Behaviors.setup(
            context -> {
                // Sensors
                ActorRef<TemperatureChangedMessage> temperatureSensor = context.spawn(
                    TemperatureSensorActor.create(),
                    "temperature-sensor"
                );
                ActorRef<WeatherChangedMessage> weatherSensor = context.spawn(
                    WeatherSensorActor.create(),
                    "weather-sensor"
                );

                // Environment
                ActorRef<Message> environment = context.spawn(
                    EnvironmentActor.create(temperatureSensor, weatherSensor),
                    "environment"
                );

                // Init environment
                environment.tell(new InitEnvironmentMessage());

                // Stop on termination
                return Behaviors.receive(Void.class)
                    .onSignal(Terminated.class, sig -> Behaviors.stopped())
                    .build();
            }
        );
    }

    public static void main(String[] args) {
        ActorSystem.create(create(), "SmartHomeSystem");
    }
}
