package at.fhv.dluvgo.hotel.write.event;

import java.util.UUID;

public class RoomCreatedEvent extends Event {
    private final UUID roomNumber;
    private final int capacity;

    public RoomCreatedEvent(UUID roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    public UUID getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }
}
