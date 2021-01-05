package at.fhv.dluvgo.hotel.read.cqrs.query;

import at.fhv.dluvgo.hotel.read.domain.Booking;
import java.util.List;

public interface GetBookingsQueryable {

    List<Booking> handle(GetBookingsQuery query);
}
