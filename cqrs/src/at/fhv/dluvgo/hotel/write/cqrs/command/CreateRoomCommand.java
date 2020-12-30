package at.fhv.dluvgo.hotel.write.cqrs.command;

import java.util.UUID;

public class CreateRoomCommand {
    private final UUID roomNumber;
    private final int maxPeople;

    public CreateRoomCommand(UUID roomNumber, int maxPeople) {
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
