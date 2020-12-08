package at.fhv.dluvgo.smarthome.sensor.message;

import at.fhv.dluvgo.smarthome.Message;

public class WeatherChangedMessage implements Message {
    private final int weather;

    public WeatherChangedMessage(int weather) {
        this.weather = weather;
    }

    public int getWeather() {
        return weather;
    }
}
