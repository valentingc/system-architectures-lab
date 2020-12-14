package at.fhv.dluvgo.smarthome.environment.message;

import at.fhv.dluvgo.smarthome.Message;

public class WeatherChangeRequestMessage implements Message {
    private final boolean weatherOverride;
    private int weather;

    public WeatherChangeRequestMessage() {
        this.weatherOverride = false;
    }

    public WeatherChangeRequestMessage(int weather) {
        this.weatherOverride = true;
        this.weather = weather;
    }

    public boolean isWeatherOverride() {
        return weatherOverride;
    }

    public int getWeather() {
        return weather;
    }
}
