package at.fhv.dluvgo.hotel.write.utils;

import at.fhv.dluvgo.hotel.write.domain.Booking;
import at.fhv.dluvgo.hotel.write.domain.PersonalDetails;
import at.fhv.dluvgo.hotel.write.event.BookingCancelledEvent;
import at.fhv.dluvgo.hotel.write.event.BookingCreatedEvent;
import at.fhv.dluvgo.hotel.write.event.Event;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import java.util.List;
import java.util.UUID;

public class BookingUtility {

    public static Booking recreateBookingState(EventStore store, UUID bookingId) {
        Booking booking = null;
        List<Event> events = store.getEvents(bookingId);

        for (Event event : events) {
            if (event instanceof BookingCreatedEvent) {
                BookingCreatedEvent e = (BookingCreatedEvent) event;
                booking = Booking.create(
                    RoomUtility.recreateRoomState(store, e.getRoomNumber()),
                        e.getStart(),
                        e.getEnd(),
                        PersonalDetails.create(
                            e.getContactName(),
                            e.getNumberOfPeople()
                        )
                );
            } else if (event instanceof BookingCancelledEvent) {
                if (null != booking) {
                    booking.cancel();
                }
            }
        }

        return booking;
    }
}
