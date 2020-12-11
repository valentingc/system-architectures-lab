package at.fhv.dluvgo.smarthome;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import at.fhv.dluvgo.smarthome.actuators.ac.AirConditioningActor;
import at.fhv.dluvgo.smarthome.actuators.ac.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.actuators.blinds.BlindsActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.FridgeActor;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.AddProductMessage;
import at.fhv.dluvgo.smarthome.actuators.fridge.message.FridgeMessage;
import at.fhv.dluvgo.smarthome.actuators.mediastation.MediaStationActor;
import at.fhv.dluvgo.smarthome.actuators.mediastation.message.MediaPlaybackRequestMessage;
import at.fhv.dluvgo.smarthome.environment.EnvironmentActor;
import at.fhv.dluvgo.smarthome.environment.message.InitEnvironmentMessage;
import at.fhv.dluvgo.smarthome.sensor.TemperatureSensorActor;
import at.fhv.dluvgo.smarthome.sensor.WeatherSensorActor;
import at.fhv.dluvgo.smarthome.sensor.message.EnvTemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.sensor.message.EnvWeatherChangedMessage;
import java.io.IOException;

public class SmartHomeController {

    public static void main(String[] args) throws IOException {
        ActorSystem.create(create(), "SmartHomeSystem");

//        boolean running = true;
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        while (running) {
//            String input = reader.readLine();
//            if (input.equals("quit")) {
//                running = false;
//            } else if (input.equals("playMovie")) {
//
//            }
//        }
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
                environment.tell(new InitEnvironmentMessage());
                mediaStation.tell(new MediaPlaybackRequestMessage("test"));

                ActorRef<FridgeMessage> fridge = context.spawn(
                    FridgeActor.create(),
                    "fridge"
                );


                FridgeActor.Product p = new FridgeActor.Product(
                    "Demoproduct",1,1
                );
                fridge.tell(new AddProductMessage(p));
                fridge.tell(new AddProductMessage(p));
                // Stop on termination
                return Behaviors.receive(Void.class)
                    .onSignal(Terminated.class, sig -> Behaviors.stopped())
                    .build();
            }
        );
    }
}
