package at.fhv.dluvgo.hotel;

import at.fhv.dluvgo.hotel.read.RunRead;
import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.cqrs.query.GetFreeRoomsQuery;
import at.fhv.dluvgo.hotel.write.RunWrite;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import at.fhv.dluvgo.hotel.write.cqrs.command.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

        RunWrite wt = new RunWrite();
        Thread wtThread = new Thread(wt);

        RunRead rr = new RunRead();
        Thread rrThread = new Thread(rr);

        EventStore eventStore = wt.getEventStore();
        eventStore.subscribe(rr.getBookingProjector());
        eventStore.subscribe(rr.getRoomProjector());

        wtThread.start();
        rrThread.start();

        wt.runCommand(new CreateRoomCommand(UUID.randomUUID(), 2));


        List<BookableRoom> result = rr.runQuery(
            new GetFreeRoomsQuery(
                LocalDateTime.of(2021, 1,5,15,0,0),
                LocalDateTime.of(2021, 1,8,9,0,0),
                2
            )
        );

        try {
            rrThread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
