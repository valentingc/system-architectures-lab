package at.fhv.dluvgo.smarthome.actuators.blinds.message;

import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.common.WeatherType;

public class WeatherChangedMessage implements Message {
    private final WeatherType weather;

    public WeatherChangedMessage(WeatherType weather) {
        this.weather = weather;
    }

    public WeatherType getWeather() {
        return weather;
    }
}
