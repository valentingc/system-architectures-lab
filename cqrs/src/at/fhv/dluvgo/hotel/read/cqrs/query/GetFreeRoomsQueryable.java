package at.fhv.dluvgo.hotel.read.cqrs.query;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import java.util.List;

public interface GetFreeRoomsQueryable {
    List<BookableRoom> handle(GetFreeRoomsQuery query);
}
