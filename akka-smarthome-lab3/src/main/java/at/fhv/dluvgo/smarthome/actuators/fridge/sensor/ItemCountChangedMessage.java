package at.fhv.dluvgo.smarthome.actuators.fridge.sensor;

import at.fhv.dluvgo.smarthome.Message;

public class ItemCountChangedMessage implements Message {

    private final int itemCount;

    public ItemCountChangedMessage(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemCount() {
        return this.itemCount;
    }
}
