package at.fhv.dluvgo.hotel.write.aggregate;

import at.fhv.dluvgo.hotel.write.cqrs.command.BookRoomCommand;
import at.fhv.dluvgo.hotel.write.cqrs.command.CancelBookingCommand;
import at.fhv.dluvgo.hotel.write.domain.Booking;
import at.fhv.dluvgo.hotel.write.domain.Room;
import at.fhv.dluvgo.hotel.write.event.BookingCancelledEvent;
import at.fhv.dluvgo.hotel.write.event.BookingCreatedEvent;
import at.fhv.dluvgo.hotel.write.event.Event;
import at.fhv.dluvgo.hotel.write.event.RoomBookedEvent;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import at.fhv.dluvgo.hotel.write.utils.BookingUtility;
import at.fhv.dluvgo.hotel.write.utils.RoomUtility;
import java.util.Arrays;
import java.util.List;

public class BookingAggregate {
    private final EventStore eventStore;

    public BookingAggregate(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<Event> handleBookRoomCommand(BookRoomCommand command) throws Exception {
        System.out.println("[WRITE] BookingAggregate - BookRoomCommand called");
        Room room = RoomUtility.recreateRoomState(eventStore, command.getRoomNumber());

        BookingCreatedEvent bookingCreatedEvent = new BookingCreatedEvent(
            room.getRoomNumber(),
            command.getBookingStartTime(),
            command.getBookingEndTime(),
            command.getContactName(),
            command.getNumberOfPeople()
        );

        RoomBookedEvent roomBookedEvent = new RoomBookedEvent(
            bookingCreatedEvent.getBookingId(),
            command.getRoomNumber(),
            command.getBookingStartTime(),
            command.getBookingEndTime(),
            command.getContactName(),
            command.getNumberOfPeople()
        );

        if (!room.isFree(command.getBookingStartTime(), command.getBookingEndTime(),
            bookingCreatedEvent.getBookingId())) {
            throw new Exception("[WRITE] BookingAggregate - Room is not free");
        }

        eventStore.addEvent(bookingCreatedEvent.getBookingId(), bookingCreatedEvent);
        eventStore.addEvent(bookingCreatedEvent.getRoomNumber(), roomBookedEvent);

        return Arrays.asList(bookingCreatedEvent, roomBookedEvent);
    }

    public List<Event> handleCancelBookingCommand(CancelBookingCommand command) throws Exception {
        System.out.println("[WRITE] BookingAggregate - CancelBookingCommand called");
        Booking booking = BookingUtility.recreateBookingState(eventStore, command.getBookingId());

        if (booking.getState().equals(Booking.State.CANCELLED)) {
            throw new Exception("[WRITE] BookingAggregate - Booking is already cancelled");
        }

        BookingCancelledEvent bookingCancelledEvent = new BookingCancelledEvent(
            command.getBookingId()
        );

        eventStore.addEvent(booking.getRoom().getRoomNumber(), bookingCancelledEvent);

        return Arrays.asList(bookingCancelledEvent);
    }
}
