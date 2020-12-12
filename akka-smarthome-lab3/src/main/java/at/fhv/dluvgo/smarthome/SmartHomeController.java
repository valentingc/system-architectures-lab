package at.fhv.dluvgo.smarthome;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import at.fhv.dluvgo.smarthome.actuators.ac.AirConditioningActor;
import at.fhv.dluvgo.smarthome.actuators.ac.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.BlindsActor;
import at.fhv.dluvgo.smarthome.actuators.cli.UserInputActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.ConsumeProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.OrderProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.RequestStoredProductsMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.MediaStationActor;
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
                ActorRef<Message> blinds = context.spawn(
                    BlindsActor.create(),
                    "blinds-actuator"
                );
                ActorRef<Message> mediaStation = context.spawn(
                    MediaStationActor.create(blinds),
                    "media-station-actuator"
                );
                ActorRef<Message> fridge = context.spawn(
                    FridgeActor.create(),
                    "fridge"
                );

                // Sensors
                ActorRef<EnvTemperatureChangedMessage> temperatureSensor = context.spawn(
                    TemperatureSensorActor.create(airConditioning),
                    "temperature-sensor"
                );
                ActorRef<EnvWeatherChangedMessage> weatherSensor = context.spawn(
                    WeatherSensorActor.create(blinds),
                    "weather-sensor"
                );

                // Environment
                ActorRef<Message> environment = context.spawn(
                    EnvironmentActor.create(temperatureSensor, weatherSensor),
                    "environment"
                );

                // Init environment
                ActorRef<Message> cli = context.spawn(
                    UserInputActor.create(fridge, mediaStation),
                    "cli"
                );
                environment.tell(new InitEnvironmentMessage());

                // TODO: fix this; separate into multiple messages so we don't need null

                FridgeActor.Product p = FridgeActor.Product.MILCH_SCHNITTE;
                fridge.tell(new OrderProductMessage(p, cli, null));
                Thread.sleep(500);
                fridge.tell(new ConsumeProductMessage(p, replyTo));
                Thread.sleep(500);
                fridge.tell(new OrderProductMessage(p, cli, null));
                Thread.sleep(200);
                fridge.tell(new OrderProductMessage(p, cli, null));
                Thread.sleep(200);
                fridge.tell(new OrderProductMessage(p, cli, null));
                Thread.sleep(200);
                fridge.tell(new OrderProductMessage(p, cli, null));
                Thread.sleep(200);
                fridge.tell(new OrderProductMessage(p, cli, null));
                Thread.sleep(200);
                fridge.tell(new RequestStoredProductsMessage(cli));
                // Stop on termination
                return Behaviors.receive(Void.class)
                    .onSignal(Terminated.class, sig -> Behaviors.stopped())
                    .build();
            }
        );
    }
}
