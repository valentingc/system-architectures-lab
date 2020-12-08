package at.fhv.dluvgo.smarthome.actuators.message;

import at.fhv.dluvgo.smarthome.Message;
import at.fhv.dluvgo.smarthome.common.Temperature;

public class TemperatureChangedMessage implements Message {
    private final Temperature temperature;

    public TemperatureChangedMessage(Temperature temperature) {
        this.temperature = temperature;
    }

    public Temperature getTemperature() {
        return temperature;
    }
}
