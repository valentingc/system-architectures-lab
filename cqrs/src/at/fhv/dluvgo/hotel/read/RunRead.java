package at.fhv.dluvgo.hotel.read;

import at.fhv.dluvgo.hotel.read.cqrs.query.GetBookingsQuery;
import at.fhv.dluvgo.hotel.read.cqrs.query.GetFreeRoomsQuery;
import at.fhv.dluvgo.hotel.read.cqrs.query.QueryHandler;
import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
import at.fhv.dluvgo.hotel.read.projection.BookingProjector;
import at.fhv.dluvgo.hotel.read.projection.RoomProjector;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import java.util.List;

public class RunRead implements Runnable {
    private final ReadRepository readRepository;
    private final BookingProjector bookingProjector;
    private final RoomProjector roomProjector;
    private final QueryHandler queryHandler;
    private boolean isActive;

    public RunRead() {
        this.readRepository = new ReadRepository();
        this.bookingProjector = new BookingProjector(readRepository);
        this.roomProjector = new RoomProjector(readRepository);
        this.queryHandler = new QueryHandler(readRepository);
        this.isActive = true;
    }

    @Override
    public void run() {
        while (isActive) {
        }
    }

    public BookingProjector getBookingProjector() {
        return bookingProjector;
    }

    public RoomProjector getRoomProjector() {
        return roomProjector;
    }

    public List<Booking> runQuery(GetBookingsQuery query) {
        return queryHandler.handle(query);
    }

    public List<BookableRoom> runQuery(GetFreeRoomsQuery query) {
        return queryHandler.handle(query);
    }
}
