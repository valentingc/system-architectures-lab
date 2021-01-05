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
    public static final int CHECKIN_HOUR = 14;
    public static final int CHECKOUT_HOUR = 11;
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

    private void splitBookableRoom(BookableRoom room, RoomBookedEvent event) {
        this.readRepository.addBookableRoom(new BookableRoom(
            room.getRoomNumber(),
            room.getStart()
                .withHour(CHECKIN_HOUR)
                .withMinute(0)
                .withSecond(0),
            event.getBookingStartTime().toLocalDate().atTime(CHECKOUT_HOUR, 0),
            room.getCapacity()
        ));

        if (event.getBookingEndTime().getHour() > CHECKOUT_HOUR) {
            this.readRepository.addBookableRoom(new BookableRoom(
                room.getRoomNumber(),
                event.getBookingEndTime().plusDays(1)
                    .withHour(CHECKIN_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getEnd()
                    .withHour(CHECKOUT_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getCapacity()
            ));
        } else {
            this.readRepository.addBookableRoom(new BookableRoom(
                room.getRoomNumber(),
                event.getBookingEndTime().withHour(CHECKIN_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getEnd().withHour(CHECKOUT_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getCapacity()
            ));
        }
        this.readRepository.removeBookableRoom(room);
    }

    private void addBookableRoomAfter(BookableRoom room, RoomBookedEvent event) {
        if (event.getBookingEndTime().getHour() > CHECKOUT_HOUR) {
            this.readRepository.addBookableRoom(new BookableRoom(
                room.getRoomNumber(),
                event.getBookingEndTime().plusDays(1)
                    .withHour(CHECKIN_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getEnd()
                    .withHour(CHECKOUT_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getCapacity()
            ));
        } else {
            this.readRepository.addBookableRoom(new BookableRoom(
                room.getRoomNumber(),
                event.getBookingEndTime()
                    .withHour(CHECKIN_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getEnd()
                    .withHour(CHECKOUT_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getCapacity()
            ));
        }
        this.readRepository.removeBookableRoom(room);
    }

    private void addBookableRoomBefore(BookableRoom room, RoomBookedEvent event) {
        if (event.getBookingStartTime().getHour() < CHECKIN_HOUR) {
            this.readRepository.addBookableRoom(new BookableRoom(
                room.getRoomNumber(),
                room.getStart()
                    .withHour(CHECKIN_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                event.getBookingStartTime().minusDays(1)
                    .withHour(CHECKOUT_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getCapacity()
            ));
        } else {
            this.readRepository.addBookableRoom(new BookableRoom(
                room.getRoomNumber(),
                room.getStart().withHour(CHECKIN_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                event.getBookingStartTime().withHour(CHECKOUT_HOUR)
                    .withMinute(0)
                    .withSecond(0),
                room.getCapacity()
            ));
        }
        this.readRepository.removeBookableRoom(room);
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
            if (br.getStart().isBefore(event.getBookingStartTime())
                && br.getEnd().isAfter(event.getBookingEndTime())
            ) {
                // case 1: new booking is in middle of a free spot
                currentBookableRoom = br;
                this.splitBookableRoom(currentBookableRoom, event);
                break;
            } else if (br.getStart().isEqual(event.getBookingStartTime())) {
                // case 2: booking is on left end
                currentBookableRoom = br;
                this.addBookableRoomAfter(currentBookableRoom, event);
                break;
            } else if (br.getEnd().isEqual(event.getBookingEndTime())) {
                // case 3: booking is on right end
                currentBookableRoom = br;
                this.addBookableRoomBefore(currentBookableRoom, event);
                break;
            }
        }
        if (null == currentBookableRoom) {
            System.err.println("Something bad happened while handling a [RoomBooked] event");
        }
    }
}
