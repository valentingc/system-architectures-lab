package at.fhv.dluvgo.cqrs.hotel.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Room {
    private final UUID roomNumber;
    private final int maxPeople;
    private final List<Booking> bookings = new LinkedList<>();

    public Room() {
        this(new Random().nextInt(4));
    }

    public Room(int maxPeople) {
        roomNumber = UUID.randomUUID();
        this.maxPeople = maxPeople;
    }

    /* ### Business logic ### */

    public boolean isFree(LocalDateTime start, LocalDateTime end) {
        for (Booking booking : bookings) {
            if (booking.getStart().isBefore(end) && start.isBefore(booking.getEnd())) {
                return false;
            }
        }

        return true;
    }

    public void addBooking(Booking booking) {
        if (!roomNumber.equals(booking.getRoom().getRoomNumber())) {
            throw new IllegalArgumentException("Booking does not belong to this room");
        }

        bookings.add(booking);
    }

    /* ### Getter ### */

    public UUID getRoomNumber() {
        return roomNumber;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }
}
