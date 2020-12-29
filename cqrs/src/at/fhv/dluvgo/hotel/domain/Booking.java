package at.fhv.dluvgo.hotel.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {
    private final UUID id;
    private final Room room;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final PersonalDetails personalDetails;

    private Booking(
        Room room,
        LocalDateTime start,
        LocalDateTime end,
        PersonalDetails personalDetails
    ) {
        this.id = UUID.randomUUID();
        this.room = room;
        this.start = start;
        this.end = end;
        this.personalDetails = personalDetails;
    }

    public static Booking create(
        Room room,
        LocalDateTime start,
        LocalDateTime end,
        PersonalDetails personalDetails
    ) {
        if (room.getMaxPeople() < personalDetails.getNumberOfPeople()) {
            throw new IllegalArgumentException("The room is too small to accommodate all people");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (!room.isFree(start, end)) {
            throw new IllegalArgumentException("Room is already booked during this time frame");
        }

        Booking booking = new Booking(room, start, end, personalDetails);

        room.addBooking(booking);
        return booking;
    }

    /* ### Business logic ### */

    // TODO - ?

    /* ### Getter ### */

    public UUID getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }
}
