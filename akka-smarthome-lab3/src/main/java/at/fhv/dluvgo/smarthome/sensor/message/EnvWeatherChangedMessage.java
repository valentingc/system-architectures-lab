package at.fhv.dluvgo.smarthome.sensor.message;

import at.fhv.dluvgo.smarthome.Message;

public class EnvWeatherChangedMessage implements Message {
    private final int weather;

    public EnvWeatherChangedMessage(int weather) {
        this.weather = weather;
    }

    public int getWeather() {
        return weather;
    }
}
