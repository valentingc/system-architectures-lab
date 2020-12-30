package at.fhv.dluvgo.hotel.write.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingCreatedEvent extends Event {
    private final UUID roomNumber;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String contactName;
    private final int numberOfPeople;

    public BookingCreatedEvent(
        UUID roomNumber,
        LocalDateTime start,
        LocalDateTime end,
        String contactName,
        int numberOfPeople
    ) {
        this.roomNumber = roomNumber;
        this.start = start;
        this.end = end;
        this.contactName = contactName;
        this.numberOfPeople = numberOfPeople;
    }

    public UUID getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getContactName() {
        return contactName;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }
}
