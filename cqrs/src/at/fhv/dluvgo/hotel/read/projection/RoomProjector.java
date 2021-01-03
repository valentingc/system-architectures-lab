package at.fhv.dluvgo.hotel.read.projection;

import at.fhv.dluvgo.hotel.read.domain.BookableRoom;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import at.fhv.dluvgo.hotel.write.event.Event;
import at.fhv.dluvgo.hotel.write.event.RoomBookedEvent;
import at.fhv.dluvgo.hotel.write.event.RoomCreatedEvent;
import java.util.List;

public class RoomProjector implements Observer {
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
            START_OF_DAY,
            END_OF_DAY,
            event.getCapacity()
        ));
    }

    private void apply(RoomBookedEvent event) {
        List<BookableRoom> bookableRooms = this.readRepository.getBookableRooms(
            event.getRoomNumber()
        );

        BookableRoom bookableRoom = null;

        if (bookableRooms.isEmpty()) {
            System.err.println(
                "Something bad happened.. let's pretend it never happened (like Trump.. or Corona)"
            );
        } else if (bookableRooms.size() < 2) {
            bookableRoom = bookableRooms.get(0);
            this.readRepository.removeBookableRoom(bookableRoom);
        } else {
            for (BookableRoom br : bookableRooms) {
                if (!br.getStart().isAfter(event.getBookingEndTime()) &&
                    !event.getBookingStartTime().isAfter(br.getEnd())
                ) {
                    bookableRoom = br;
                }
            }

            // remove bookableRoom
            BookableRoom before = null;
            for (BookableRoom br : bookableRooms) {
                if (br.getStart().isBefore(event.getBookingStartTime())) {
                    before = br;
                }
            }

            BookableRoom after = null;
            for (BookableRoom br : bookableRooms) {
                if (br.getStart().isAfter(event.getBookingEndTime())) {
                    after = br;
                    break;
                }
            }

            this.readRepository.removeBookableRoom((bookableRoom));
            this.readRepository.addBookableRoom(
                new BookableRoom(
                    bookableRoom.getRoomNumber(),
                    before.getEnd(),
                    event.getBookingStartTime(),
                    bookableRoom.getCapacity()
                )
            );
            this.readRepository.addBookableRoom(
                new BookableRoom(
                    bookableRoom.getRoomNumber(),
                    event.getBookingEndTime(),
                    after.getStart(),
                    bookableRoom.getCapacity()
                )
            );

        }
    }
}
