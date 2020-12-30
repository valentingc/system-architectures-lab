package at.fhv.dluvgo.hotel.write.event;

import java.util.UUID;

public class RoomCreatedEvent extends Event {
    private final UUID roomNumber;
    private final int maxPeople;

    public RoomCreatedEvent(UUID roomNumber, int maxPeople) {
        this.roomNumber = roomNumber;
        this.maxPeople = maxPeople;
    }

    public UUID getRoomNumber() {
        return roomNumber;
    }

    public int getMaxPeople() {
        return maxPeople;
    }
}
