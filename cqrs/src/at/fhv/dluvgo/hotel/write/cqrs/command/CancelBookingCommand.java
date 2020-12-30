package at.fhv.dluvgo.hotel.write.cqrs.command;

import java.util.UUID;

public class CancelBookingCommand {
    private final UUID bookingId;

    public CancelBookingCommand(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getBookingId() {
        return bookingId;
    }
}
