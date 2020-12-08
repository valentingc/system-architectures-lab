package at.fhv.dluvgo.smarthome.sensor.message;

import at.fhv.dluvgo.smarthome.Message;

public class EnvTemperatureChangedMessage implements Message {
    private final float temperature;

    public EnvTemperatureChangedMessage(float temperature) {
        this.temperature = temperature;
    }

    public float getTemperature() {
        return temperature;
    }
}
