package at.fhv.dluvgo.hotel.write;

import at.fhv.dluvgo.hotel.read.cqrs.query.GetBookingsQuery;
import at.fhv.dluvgo.hotel.read.cqrs.query.GetFreeRoomsQuery;
import at.fhv.dluvgo.hotel.read.cqrs.query.QueryHandler;
import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
import at.fhv.dluvgo.hotel.read.projection.BookingProjector;
import at.fhv.dluvgo.hotel.read.projection.RoomProjector;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import at.fhv.dluvgo.hotel.write.aggregate.BookingAggregate;
import at.fhv.dluvgo.hotel.write.aggregate.RoomAggregate;
import at.fhv.dluvgo.hotel.write.cqrs.command.BookRoomCommand;
import at.fhv.dluvgo.hotel.write.cqrs.command.CancelBookingCommand;
import at.fhv.dluvgo.hotel.write.cqrs.command.CreateRoomCommand;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import java.util.List;

public class RunWrite implements Runnable {
    private final EventStore eventStore;
    private boolean isActive;

    BookingAggregate bookingAggregate;
    RoomAggregate roomAggregate;

    public RunWrite() {
        this.isActive = true;
        eventStore = new EventStore();
        bookingAggregate = new BookingAggregate(eventStore);
        roomAggregate = new RoomAggregate(eventStore);
    }

    public EventStore getEventStore() {
        return eventStore;
    }

    @Override
    public void run() {
        while (isActive) {
        }
    }

    public void runCommand(BookRoomCommand cmd) {
        try {
            this.bookingAggregate.handleBookRoomCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runCommand(CancelBookingCommand cmd) {
        try {
            this.bookingAggregate.handleCancelBookingCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runCommand(CreateRoomCommand cmd) {
        this.roomAggregate.handleCreateRoomCommand(cmd);
    }
}
