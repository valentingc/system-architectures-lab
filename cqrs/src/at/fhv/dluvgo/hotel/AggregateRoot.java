package at.fhv.dluvgo.hotel;

import at.fhv.dluvgo.hotel.events.Event;
import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    private final List<Event> events = new ArrayList<>();

}
