package at.fhv.dluvgo.hotel.write.repository;

import at.fhv.dluvgo.hotel.write.event.Event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventStore {
    private Map<UUID, List<Event>> store = new HashMap<>();

    public void addEvent(UUID id, Event event) {
        List<Event> events = store.get(id);
        if (events == null) {
            events = new ArrayList<>();
            events.add(event);
            store.put(id, events);
        } else {
            events.add(event);
        }
    }

    public List<Event> getEvents(UUID id) {
        return store.get(id);
    }
}
