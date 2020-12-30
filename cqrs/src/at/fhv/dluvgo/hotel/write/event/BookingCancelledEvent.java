package at.fhv.dluvgo.hotel.write.event;

import java.util.UUID;

public class BookingCancelledEvent extends Event {
    private final UUID bookingId;

    public BookingCancelledEvent(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getBookingId() {
        return bookingId;
    }
}
