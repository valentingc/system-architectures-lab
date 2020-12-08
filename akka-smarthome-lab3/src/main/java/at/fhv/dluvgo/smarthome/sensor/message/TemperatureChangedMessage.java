package at.fhv.dluvgo.smarthome.sensor.message;

import at.fhv.dluvgo.smarthome.Message;

public class TemperatureChangedMessage implements Message {
    private final float temperature;

    public TemperatureChangedMessage(float temperature) {
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }
}
