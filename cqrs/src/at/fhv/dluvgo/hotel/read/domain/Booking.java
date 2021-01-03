package at.fhv.dluvgo.hotel.read.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Booking {
    public enum State {
        ACTIVE,
        CANCELLED
    }

    private final UUID id;
    private final UUID roomNumber;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private State state;
    private final String bookingMadeBy;
    private final int numberOfPeople;

    public Booking(
        UUID id,
        UUID roomNumber,
        LocalDateTime start,
        LocalDateTime end,
        State state,
        String bookingMadeBy,
        int numberOfPeople
    ) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.start = start;
        this.end = end;
        this.state = state;
        this.bookingMadeBy = bookingMadeBy;
        this.numberOfPeople = numberOfPeople;
    }

    public Booking(Booking oldBooking, State newState) {
        this.id = oldBooking.id;
        this.roomNumber = oldBooking.roomNumber;
        this.start = oldBooking.start;
        this.end = oldBooking.end;
        this.state = newState;
        this.bookingMadeBy = oldBooking.bookingMadeBy;
        this.numberOfPeople = oldBooking.numberOfPeople;
    }

    /* ### Getter ### */

    public UUID getId() {
        return id;
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

    public State getState() {
        return state;
    }

    public String getBookingMadeBy() {
        return bookingMadeBy;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    /* ### Setter ### */

    public void setState(State state) {
        this.state = state;
    }

    /* ### General ### */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking booking = (Booking) o;
        return id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
