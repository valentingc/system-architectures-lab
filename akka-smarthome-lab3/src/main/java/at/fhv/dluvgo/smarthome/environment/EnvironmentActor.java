package at.fhv.dluvgo.smarthome.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.common.WeatherType;
import at.fhv.dluvgo.smarthome.environment.message.InitEnvironmentMessage;
import at.fhv.dluvgo.smarthome.environment.message.TemperatureChangeRequestMessage;
import at.fhv.dluvgo.smarthome.environment.message.WeatherChangeRequestMessage;
import at.fhv.dluvgo.smarthome.sensor.TemperatureSensorActor;
import at.fhv.dluvgo.smarthome.sensor.WeatherSensorActor;
import at.fhv.dluvgo.smarthome.sensor.message.TemperatureChangedMessage;
import at.fhv.dluvgo.smarthome.sensor.message.WeatherChangedMessage;
import java.time.Duration;
import java.util.Random;

public class EnvironmentActor extends AbstractBehavior<Message> {
    private static class Timeout implements Message {
        // TODO - Actually needed?
    }

    private static final Object TEMPERATURE_TIMER_KEY = new Object();
    private static final Object WEATHER_TIMER_KEY = new Object();

    private final ActorRef<TemperatureChangedMessage> temperatureSensor;
    private final ActorRef<WeatherChangedMessage> weatherSensor;

    private final TimerScheduler<Message> temperatureTimer;
    private final TimerScheduler<Message> weatherTimer;

    private int currentWeather = 0;
    private float currentTemperature = 22.0f;

    /* ### Instance of EnvironmentActor ###*/

    public static Behavior<Message> create(
        ActorRef<TemperatureChangedMessage> temperatureSensor,
        ActorRef<WeatherChangedMessage> weatherSensor
    ) {
        return Behaviors.setup(ctx -> Behaviors.withTimers(timers -> new EnvironmentActor(
            ctx,
            timers,
            timers,
            temperatureSensor,
            weatherSensor
        )));
    }

    private EnvironmentActor(
        ActorContext<Message> context,
        TimerScheduler<Message> temperatureTimer,
        TimerScheduler<Message> weatherTimer,
        ActorRef<TemperatureChangedMessage> temperatureSensor,
        ActorRef<WeatherChangedMessage> weatherSensor
    ) {
        super(context);
        this.temperatureTimer = temperatureTimer;
        this.weatherTimer = weatherTimer;
        this.temperatureSensor = temperatureSensor;
        this.weatherSensor = weatherSensor;
    }

    /* ### Message handling ### */

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(InitEnvironmentMessage.class, this::onInitEnvironment)
            .onMessage(TemperatureChangeRequestMessage.class, this::onTemperatureChangeRequest)
            .onMessage(WeatherChangeRequestMessage.class, this::onWeatherChangeRequest)
            .onMessage(Timeout.class, message -> onTimeout())
            .build();
    }

    /**
     * Initializes the environment by starting a timer for changing the temperature and
     * {@link WeatherType} regularly.
     *
     * @param msg The message
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onInitEnvironment(Message msg) {
        getContext().getLog().info("Initializing environment..");

        temperatureTimer.startTimerAtFixedRate(
            TEMPERATURE_TIMER_KEY,
            new TemperatureChangeRequestMessage(),
            Duration.ofSeconds(1) // TODO - Find best value
        );

        weatherTimer.startTimerAtFixedRate(
            WEATHER_TIMER_KEY,
            new WeatherChangeRequestMessage(),
            Duration.ofSeconds(5) // TODO - Find best value
        );

        return this;
    }

    /**
     * Handles {@link TemperatureChangeRequestMessage} by randomly increasing
     * or decreasing the temperature.
     *
     * @param msg The request
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onTemperatureChangeRequest(TemperatureChangeRequestMessage msg) {
        float tempDifference = 0.0f;
        if (currentTemperature <= 15.0) {
            tempDifference = +3.0f;
        } else if (currentTemperature >= 28.5) {
            tempDifference = -3.0f;
        }

        currentTemperature += tempDifference;
        if (new Random().nextInt(10) >= 5) {
            currentTemperature -= 0.7;
        } else {
            currentTemperature += 0.7;
        }

        temperatureSensor.tell(new TemperatureChangedMessage(currentTemperature));
        getContext().getLog().info("Current temperature now is: {}", currentTemperature);
        return this;
    }

    /**
     * Handles {@link WeatherChangeRequestMessage} by randomly selecting a new {@link WeatherType}.
     *
     * @param msg The request
     *
     * @return this - no change of behaviour
     */
    private Behavior<Message> onWeatherChangeRequest(WeatherChangeRequestMessage msg) {
        currentWeather = new Random().nextInt(WeatherType.values().length);

        weatherSensor.tell(new WeatherChangedMessage(currentWeather));
        getContext().getLog().info(
            "Current weather now is: {}",
            WeatherType.values()[currentWeather]
        );
        return this;
    }

    // TODO
    private Behavior<Message> onTimeout() {
        getContext().getLog().info("Timeout received");
        return this;
    }
}
