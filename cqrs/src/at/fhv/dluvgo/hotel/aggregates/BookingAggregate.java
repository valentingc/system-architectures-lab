package at.fhv.dluvgo.hotel.aggregates;

import at.fhv.dluvgo.hotel.repository.EventStore;

public class BookingAggregate {
    private EventStore eventStore;

    public BookingAggregate(EventStore eventStore) {
        this.eventStore = eventStore;
    }
}
