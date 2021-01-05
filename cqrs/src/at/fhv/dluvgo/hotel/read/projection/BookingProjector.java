package at.fhv.dluvgo.hotel.read.projection;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import at.fhv.dluvgo.hotel.write.event.BookingCancelledEvent;
import at.fhv.dluvgo.hotel.write.event.BookingCreatedEvent;
import at.fhv.dluvgo.hotel.write.event.Event;
import java.util.List;

public class BookingProjector implements Observer {
    private final ReadRepository readRepository;

    public BookingProjector(ReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Override
    public void update(Event event) {
        System.out.println("[READ] BookingProjector - called");
        if (event instanceof BookingCreatedEvent) {
            this.apply((BookingCreatedEvent) event);
        } else if (event instanceof BookingCancelledEvent) {
            this.apply((BookingCancelledEvent) event);
        }
    }

    private void apply(BookingCreatedEvent event) {
        this.readRepository.addBooking(new Booking(
            event.getBookingId(),
            event.getRoomNumber(),
            event.getStart(),
            event.getEnd(),
            Booking.State.ACTIVE,
            event.getContactName(),
            event.getNumberOfPeople()
        ));
    }

    private void apply(BookingCancelledEvent event) {
        Booking bookingToUpdate = this.readRepository.getBooking(event.getBookingId());
        bookingToUpdate.setState(Booking.State.CANCELLED);

        // same logic as in roomprojector
        List<BookableRoom> bookableRooms = this.readRepository.getBookableRooms(
            this.readRepository.getBooking(event.getBookingId()).getRoomNumber()
        );

        if (bookableRooms.isEmpty()) {
            System.err.println(
                "[READ] BookingProjector - Something bad happened.. let's pretend it never did (like Trump.. or Corona)"
            );
            return;
        }

        BookableRoom leftBookableRoom = null;
        BookableRoom rightBookableRoom = null;
        // CASE 1
        for (BookableRoom br : bookableRooms) {
            // case 1
            if (br.getEnd().getDayOfMonth() == bookingToUpdate.getStart().getDayOfMonth()
                && br.getEnd().isBefore(bookingToUpdate.getStart())
            ) {
                leftBookableRoom = br;
            } else if (br.getStart().getDayOfMonth() == bookingToUpdate.getEnd().getDayOfMonth()
                && br.getStart().isAfter(bookingToUpdate.getEnd())
            ) {
                rightBookableRoom = br;
            }

            if (leftBookableRoom != null && rightBookableRoom != null) {
                this.uniteBookableRoom(leftBookableRoom, rightBookableRoom, bookingToUpdate);
                return;
            }
        }

        // Case2
        for (BookableRoom br : bookableRooms) {
            if (br.getStart().getDayOfMonth() == bookingToUpdate.getEnd().getDayOfMonth()
                && br.getStart().isAfter(bookingToUpdate.getEnd())) {
                // Case 2
                this.extendBookableRoomLeft(br, bookingToUpdate);
                return;
            }
        }

        // Case3
        for (BookableRoom br : bookableRooms) {
            if (br.getEnd().getDayOfMonth() == bookingToUpdate.getStart().getDayOfMonth()
                && br.getEnd().isBefore(bookingToUpdate.getStart())) {
                // Case
                this.extendBookableRoomRight(br, bookingToUpdate);
                return;
            }
        }

        this.readRepository.removeBooking(bookingToUpdate);
    }

    private void uniteBookableRoom(
        BookableRoom leftBookableRoom,
        BookableRoom rightBookableRoom,
        Booking cancelledBooking
    ) {
        System.out.println("[READ] BookingProjector - uniting two separate BookableRooms");
        this.readRepository.removeBookableRoom(leftBookableRoom);
        this.readRepository.removeBookableRoom(rightBookableRoom);
        this.readRepository.addBookableRoom(new BookableRoom(
            cancelledBooking.getRoomNumber(),
            leftBookableRoom.getStart()
                .withHour(RoomProjector.CHECKIN_HOUR)
                .withMinute(0)
                .withSecond(0),
            rightBookableRoom.getEnd()
                .withHour(RoomProjector.CHECKOUT_HOUR)
                .withMinute(0)
                .withSecond(0),
            leftBookableRoom.getCapacity()
        ));
    }

    private void extendBookableRoomLeft(
        BookableRoom currentBookableRoom,
        Booking currentBooking
    ) {
        System.out.println("[READ] BookingProjector - extending BookableRoom to the left");
        this.readRepository.removeBookableRoom(currentBookableRoom);
        this.readRepository.addBookableRoom(new BookableRoom(
            currentBooking.getRoomNumber(),
            currentBookableRoom.getStart()
                .withHour(RoomProjector.CHECKIN_HOUR)
                .withMinute(0)
                .withSecond(0),
            currentBooking.getEnd()
                .withHour(RoomProjector.CHECKOUT_HOUR)
                .withMinute(0)
                .withSecond(0),
            currentBookableRoom.getCapacity()
        ));
    }

    private void extendBookableRoomRight(
        BookableRoom currentBookableRoom,
        Booking currentBooking
    ) {
        System.out.println("[READ] BookingProjector - extending BookableRoom to the right");
        this.readRepository.removeBookableRoom(currentBookableRoom);
        this.readRepository.addBookableRoom(new BookableRoom(
            currentBooking.getRoomNumber(),
            currentBooking.getStart()
                .withHour(RoomProjector.CHECKIN_HOUR)
                .withMinute(0)
                .withSecond(0),
            currentBookableRoom.getEnd()
                .withHour(RoomProjector.CHECKOUT_HOUR)
                .withMinute(0)
                .withSecond(0),
            currentBookableRoom.getCapacity()
        ));
    }
}
