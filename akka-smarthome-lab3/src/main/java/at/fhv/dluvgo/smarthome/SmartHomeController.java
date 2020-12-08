package at.fhv.dluvgo.smarthome;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import at.fhv.dluvgo.smarthome.actuators.AirConditioningActor;
import at.fhv.dluvgo.smarthome.actuators.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.environment.EnvironmentActor;
import at.fhv.dluvgo.smarthome.environment.message.InitEnvironmentMessage;
import at.fhv.dluvgo.smarthome.sensor.TemperatureSensorActor;
import at.fhv.dluvgo.smarthome.sensor.WeatherSensorActor;
import at.fhv.dluvgo.smarthome.sensor.message.EnvTemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.sensor.message.EnvWeatherChangedMessage;

public class SmartHomeController {

    public static void main(String[] args) {
        ActorSystem.create(create(), "SmartHomeSystem");
    }

    /**
     * Initializes all actors (actuators, sensors and the environment) and sends initial
     * {@link InitEnvironmentMessage} to start all automation processes.
     *
     * @return Configured {@link Behavior}
     */
    private static Behavior<Void> create() {
        return Behaviors.setup(
            context -> {
                // Actuators
                ActorRef<TemperatureChangedMessage> airConditioning = context.spawn(
                    AirConditioningActor.create(),
                    "ac-actuator"
                );

                // Sensors
                ActorRef<EnvTemperatureChangedMessage> temperatureSensor = context.spawn(
                    TemperatureSensorActor.create(airConditioning),
                    "temperature-sensor"
                );
                ActorRef<EnvWeatherChangedMessage> weatherSensor = context.spawn(
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
}
