package at.fhv.dluvgo.smarthome;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import at.fhv.dluvgo.smarthome.messages.InitEnvironmentMessage;
import at.fhv.dluvgo.smarthome.messages.Message;
import at.fhv.dluvgo.smarthome.messages.TemperatureChangeRequestMessage;
import at.fhv.dluvgo.smarthome.messages.WeatherChangeRequestMessage;
import java.time.Duration;
import java.util.Random;

public class EnvironmentActor extends AbstractBehavior<Message> {
    private enum WeatherType {
        SUNNY,
        CLOUDY,
        RAINY;
    }

    private static class Timeout implements Message {
        // TODO - Actually needed?
    }

    private static final Object TEMPERATURE_TIMER_KEY = new Object();
    private static final Object WEATHER_TIMER_KEY = new Object();

    private final TimerScheduler<Message> temperatureTimer;
    private final TimerScheduler<Message> weatherTimer;

    private WeatherType currentWeather = WeatherType.SUNNY;
    private float currentTemperature = 22.0f;

    /* ### Instance of EnvironmentActor ###*/

    public static Behavior<Message> create() {
        return Behaviors.setup(ctx -> Behaviors.withTimers(timers -> new EnvironmentActor(
            ctx,
            timers,
            timers
        )));
    }

    private EnvironmentActor(
        ActorContext<Message> context,
        TimerScheduler<Message> temperatureTimer,
        TimerScheduler<Message> weatherTimer
    ) {
        super(context);
        this.temperatureTimer = temperatureTimer;
        this.weatherTimer = weatherTimer;
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
        getContext().getLog().debug("Initializing environment..");

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
        currentWeather = WeatherType.values()[new Random().nextInt(WeatherType.values().length)];

        getContext().getLog().info("Current weather now is: {}", currentWeather);
        return this;
    }

    private Behavior<Message> onTimeout() {
        getContext().getLog().debug("Timeout received");
        return this;
    }
}
