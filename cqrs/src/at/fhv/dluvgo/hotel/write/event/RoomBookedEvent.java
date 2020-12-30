package at.fhv.dluvgo.hotel.write.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoomBookedEvent extends Event {
    private final UUID roomNumber;
    private final LocalDateTime bookingStartTime;
    private final LocalDateTime bookingEndTime;
    private final String contactName;
    private final int numberOfPeople;

    public RoomBookedEvent(
        UUID roomNumber,
        LocalDateTime bookingStartTime,
        LocalDateTime bookingEndTime,
        String contactName,
        int numberOfPeople
    ) {
        this.roomNumber = roomNumber;
        this.bookingStartTime = bookingStartTime;
        this.bookingEndTime = bookingEndTime;
        this.contactName = contactName;
        this.numberOfPeople = numberOfPeople;
    }

    public UUID getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getBookingStartTime() {
        return bookingStartTime;
    }

    public LocalDateTime getBookingEndTime() {
        return bookingEndTime;
    }

    public String getContactName() {
        return contactName;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }
}
