package at.fhv.dluvgo.hotel.read.projection;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import at.fhv.dluvgo.hotel.write.event.Event;
import at.fhv.dluvgo.hotel.write.event.RoomBookedEvent;
import at.fhv.dluvgo.hotel.write.event.RoomCreatedEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class RoomProjector implements Observer {
    private static final LocalDateTime START_OF_YEAR = LocalDate.of(2021, 1, 1).atStartOfDay();
    private static final LocalDateTime END_OF_YEAR = LocalDate.of(
        2021,
        12,
        31
    ).atTime(LocalTime.MAX);

    private final ReadRepository readRepository;

    public RoomProjector(ReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Override
    public void update(Event event) {
        if (event instanceof RoomCreatedEvent) {
            this.apply((RoomCreatedEvent) event);
        } else if (event instanceof RoomBookedEvent) {
            this.apply((RoomBookedEvent) event);
        }
    }

    private void apply(RoomCreatedEvent event) {
        this.readRepository.addBookableRoom(new BookableRoom(
            event.getId(),
            START_OF_YEAR,
            END_OF_YEAR,
            event.getCapacity()
        ));
    }

    private void apply(RoomBookedEvent event) {
        List<BookableRoom> bookableRooms = this.readRepository.getBookableRooms(
            event.getRoomNumber()
        );

        if (bookableRooms.isEmpty()) {
            System.err.println(
                "Something bad happened.. let's pretend it never did (like Trump.. or Corona)"
            );
            return;
        }

        BookableRoom currentBookableRoom = null;
        for (BookableRoom br : bookableRooms) {
            if (!br.getStart().isAfter(event.getBookingEndTime()) &&
                !event.getBookingStartTime().isAfter(br.getEnd())
            ) {
                currentBookableRoom = br;
            }
        }
        if (null == currentBookableRoom) {
            System.err.println("Something bad happened");
            return;
        }

        this.readRepository.removeBookableRoom(currentBookableRoom);

        if (!currentBookableRoom.getStart().isEqual(START_OF_YEAR)) {
            this.readRepository.addBookableRoom(new BookableRoom(
                currentBookableRoom.getRoomNumber(),
                START_OF_YEAR,
                currentBookableRoom.getStart(),
                currentBookableRoom.getCapacity()
            ));
        }
        if (!currentBookableRoom.getEnd().isEqual(END_OF_YEAR)) {
            this.readRepository.addBookableRoom(new BookableRoom(
                currentBookableRoom.getRoomNumber(),
                currentBookableRoom.getEnd(),
                END_OF_YEAR,
                currentBookableRoom.getCapacity()
            ));
        }
    }
}
