package at.fhv.dluvgo.hotel.read.projection;

import at.fhv.dluvgo.hotel.read.domain.Booking;
import at.fhv.dluvgo.hotel.read.repository.ReadRepository;
import at.fhv.dluvgo.hotel.write.event.BookingCancelledEvent;
import at.fhv.dluvgo.hotel.write.event.BookingCreatedEvent;
import at.fhv.dluvgo.hotel.write.event.Event;

public class BookingProjector implements Observer {
    private final ReadRepository readRepository;

    public BookingProjector(ReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Override
    public void update(Event event) {
        if (event instanceof BookingCreatedEvent) {
            this.apply((BookingCreatedEvent) event);
        } else if (event instanceof BookingCancelledEvent) {
            this.apply((BookingCancelledEvent) event);
        }
    }

    private void apply(BookingCreatedEvent event) {
        this.readRepository.addBooking(new Booking(
            event.getId(),
            event.getRoomNumber(),
            event.getStart(),
            event.getEnd(),
            Booking.State.ACTIVE,
            event.getContactName(),
            event.getNumberOfPeople()
        ));
    }

    private void apply(BookingCancelledEvent event) {
        Booking bookingToUpdate = this.readRepository.getBooking(event.getBookingId());
        bookingToUpdate.setState(Booking.State.CANCELLED);
        this.readRepository.updateBooking(bookingToUpdate);
    }
}
