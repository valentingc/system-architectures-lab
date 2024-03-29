package at.fhv.dluvgo.hotel.read.cqrs.query;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.domain.Booking;
import at.fhv.dluvgo.hotel.read.projection.RoomProjector;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import java.time.LocalDateTime;
import java.util.List;

public class QueryHandler implements GetBookingsQueryable, GetFreeRoomsQueryable {
    private final ReadRepository readRepository;

    public QueryHandler(ReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Override
    public List<Booking> handle(GetBookingsQuery query) {
        System.out.println("[READ] QueryHandler - GetBookingsQuery query called");
        return this.readRepository.getBookings(
            query.getStartTime(),
            query.getEndTime()
        );
    }

    @Override
    public List<BookableRoom> handle(GetFreeRoomsQuery query) {
        System.out.println("[READ] QueryHandler - GetFreeRoomsQuery query called");
        LocalDateTime start = query.getStartTime();
        if (query.getStartTime().getHour() < (RoomProjector.CHECKIN_HOUR)) {
            System.out.println("[READ] QueryHandler - Start time of query is before checkin "
                + "hour (11am). Query results include another additional night before " +
                "your entered starting date. Thanks for your understanding :-)");
            start = query.getStartTime()
                .minusDays(1)
                .withHour(RoomProjector.CHECKIN_HOUR)
                .withMinute(0)
                .withSecond(0);
        }

        LocalDateTime end = query.getEndTime();
        if (query.getEndTime().getHour() > (RoomProjector.CHECKOUT_HOUR)) {
            System.out.println("[READ] QueryHandler - End time of query is after checkout " +
                "hour (2pm). Query results include another additional night after " +
                "your entered ending date. Thanks for your understanding :-)");
            end = query.getEndTime()
                .plusDays(1)
                .withHour(RoomProjector.CHECKOUT_HOUR)
                .withMinute(0)
                .withSecond(0);
        }

        return this.readRepository.getBookableRooms(
            query.getRequiredRoomCapacity(),
            start,
            end
        );
    }
}
