package at.fhv.dluvgo.hotel.read.repository;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
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
        System.err.println("Booking not found");
        return null;
    }

    public void addBooking(Booking booking) {
        if (this.bookings.contains(booking)) {
            this.updateBooking(booking);
        }
        this.bookings.add(booking);
        System.out.println("Added booking");
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
            System.err.println("Booking to update not found. Fixing: adding a new booking");
            this.addBooking(booking);
            return;
        }

        Booking newBooking = new Booking(bookingToUpdate, booking.getState());

        this.bookings.remove(bookingToUpdate);
        this.bookings.add(newBooking);
        System.out.println("Updated booking");
    }

    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
        System.out.println("Removed booking");
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

    public void addBookableRoom(BookableRoom bookableRoom) {
        if (this.bookableRooms.contains(bookableRoom)) {
            System.err.println("Bookable room already saved in ReadRepository.");
            return;
        }

        this.bookableRooms.add(bookableRoom);
        System.out.println("Added bookable Room");
    }

    public void addBookableRooms(BookableRoom... bookableRooms) {
        for (BookableRoom br : bookableRooms) {
            this.addBookableRoom(br);
        }
    }

    public void removeBookableRoom(BookableRoom bookableRoom) {
        this.bookableRooms.remove(bookableRoom);
        System.out.println("Removed bookable room");
    }
}
