package at.fhv.dluvgo.hotel.read.repository;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ReadRepository {
    private final List<Booking> bookings;
    private final List<BookableRoom> bookableRooms;

    public ReadRepository() {
        this.bookings = new LinkedList<>();
        this.bookableRooms = new LinkedList<>();
    }

    /* ### Booking ### */

    public Booking getBooking(UUID bookingId) {
        for (Booking b : this.bookings) {
            if (b.getId().equals(bookingId)) {
                return b;
            }
        }
        System.err.println("[READ] ReadRepository - Booking not found");
        return null;
    }

    public List<Booking> getBookings(
        LocalDateTime from,
        LocalDateTime to
    ) {
        List<Booking> result = new LinkedList<>();
        for (Booking b : this.bookings) {
            if ((b.getStart().isEqual(from) || b.getStart().isAfter(from))
                && (b.getEnd().isEqual(to) || b.getEnd().isBefore(to))
            ) {
                result.add(b);
            }
        }

        return result;
    }

    public void addBooking(Booking booking) {
        if (this.bookings.contains(booking)) {
            this.updateBooking(booking);
        }
        this.bookings.add(booking);
        System.out.println("[READ] ReadRepository - Added booking");
    }

    public void addBookings(Booking... bookings) {
        for (Booking b : bookings) {
            this.addBooking(b);
        }
    }

    public void updateBooking(Booking booking) {
        Booking bookingToUpdate = null;

        for (Booking b : this.bookings) {
            if (b.getId().equals(booking.getId())) {
                bookingToUpdate = b;
                break;
            }
        }

        if (null == bookingToUpdate) {
            // something went utterly wrong - AND THUS.. we simply add it
            System.err.println(
                "[READ] ReadRepository - Booking to update not found. Fixing: adding a new booking"
            );
            this.addBooking(booking);
            return;
        }

        Booking newBooking = new Booking(bookingToUpdate, booking.getState());

        this.bookings.remove(bookingToUpdate);
        this.bookings.add(newBooking);
        System.out.println("[READ] ReadRepository - Updated booking");
    }

    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
        System.out.println("[READ] ReadRepository - Removed booking");
    }

    /* ### BookableRoom ### */

    public List<BookableRoom> getBookableRooms(UUID roomNumber) {
        List<BookableRoom> result = new LinkedList<>();
        for (BookableRoom br : this.bookableRooms) {
            if (br.getRoomNumber().equals(roomNumber)) {
                result.add(br);
            }
        }

        return result;
    }

    public List<BookableRoom> getBookableRooms(
        int capacity,
        LocalDateTime from,
        LocalDateTime to
    ) {
        List<BookableRoom> result = new LinkedList<>();
        for (BookableRoom br : this.bookableRooms) {
            if (br.getCapacity() >= capacity
                && (br.getStart().isEqual(from) || br.getStart().isBefore(from))
                && (br.getEnd().isEqual(to) || br.getEnd().isAfter(to))
            ) {
                result.add(br);
            }
        }

        return result;
    }

    public void addBookableRoom(BookableRoom bookableRoom) {
        if (this.bookableRooms.contains(bookableRoom)) {
            System.err
                .println("[READ] ReadRepository - Bookable room already saved in ReadRepository.");
            return;
        }

        this.bookableRooms.add(bookableRoom);
        System.out.println("[READ] ReadRepository - Added bookable room");
    }

    public void addBookableRooms(BookableRoom... bookableRooms) {
        for (BookableRoom br : bookableRooms) {
            this.addBookableRoom(br);
        }
    }

    public void removeBookableRoom(BookableRoom bookableRoom) {
        this.bookableRooms.remove(bookableRoom);
        System.out.println("[READ] ReadRepository - Removed bookable room");
    }
}
