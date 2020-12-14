package at.fhv.dluvgo.smarthome.actuators.fridge.sensor.message;

import at.fhv.dluvgo.smarthome.Message;

// TODO - Determine whether needed or not
public class ItemCountChangedMessage implements Message {
    private final int itemCount;

    public ItemCountChangedMessage(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return this.itemCount;
    }
}
