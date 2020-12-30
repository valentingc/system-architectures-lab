package at.fhv.dluvgo.hotel.write.utils;

import at.fhv.dluvgo.hotel.write.domain.Booking;
import at.fhv.dluvgo.hotel.write.domain.PersonalDetails;
import at.fhv.dluvgo.hotel.write.domain.Room;
import at.fhv.dluvgo.hotel.write.event.Event;
import at.fhv.dluvgo.hotel.write.event.RoomBookedEvent;
import at.fhv.dluvgo.hotel.write.event.RoomCreatedEvent;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import java.util.List;
import java.util.UUID;

public class RoomUtility {

    public static Room recreateRoomState(EventStore store, UUID roomId) {
        Room room = null;

        List<Event> events = store.getEvents(roomId);
        for (Event event : events) {
            if (event instanceof RoomCreatedEvent) {
                RoomCreatedEvent e = (RoomCreatedEvent) event;
                room = new Room(e.getRoomNumber(), e.getMaxPeople());
            } else if (event instanceof RoomBookedEvent) {
                RoomBookedEvent e = (RoomBookedEvent) event;
                if (null != room) {
                    room.addBooking(
                        // TODO check this exception handling
                        Booking.create(
                            room,
                            e.getBookingStartTime(),
                            e.getBookingEndTime(),
                            PersonalDetails.create(
                                e.getContactName(),
                                e.getNumberOfPeople()
                            )
                        )
                    );
                }
            }
        }

        return room;
    }
}
