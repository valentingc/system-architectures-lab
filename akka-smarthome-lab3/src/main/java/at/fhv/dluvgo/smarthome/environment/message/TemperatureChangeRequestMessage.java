package at.fhv.dluvgo.smarthome.environment.message;

import at.fhv.dluvgo.smarthome.Message;

public class TemperatureChangeRequestMessage implements Message {
    private final boolean tempOverride;
    private float temperature;

    public TemperatureChangeRequestMessage() {
        this.tempOverride = false;
    }

    public TemperatureChangeRequestMessage(float temperature) {
        this.tempOverride = true;
        this.temperature = temperature;
    }

    public boolean isTempOverride() {
        return tempOverride;
    }

    public float getTemperature() {
        return temperature;
    }
}
