package at.fhv.dluvgo.hotel.read.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookableRoom {
    private final UUID roomNumber;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final int capacity;

    public BookableRoom(UUID roomNumber, LocalDateTime start, LocalDateTime end, int capacity) {
        this.roomNumber = roomNumber;
        this.start = start;
        this.end = end;
        this.capacity = capacity;
    }

    /* ### Getter ### */

    public UUID getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "[RoomNumber] " + roomNumber + "\n" +
            "[Start] " + start + "\n" +
            "[End] " + end + "\n" +
            "[Capacity] " + capacity;
    }
}
