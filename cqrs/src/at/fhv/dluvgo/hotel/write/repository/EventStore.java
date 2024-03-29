package at.fhv.dluvgo.hotel.write.repository;

import at.fhv.dluvgo.hotel.read.projection.Observer;
import at.fhv.dluvgo.hotel.write.event.Event;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventStore implements Observable {
    private final List<Observer> observerList = new LinkedList<>();
    private final Map<UUID, List<Event>> store = new HashMap<>();

    public void addEvent(UUID id, Event event) {
        System.out.printf(
            "[WRITE] EventStore - Adding event %s to EventStore, key: %s%n",
            event.getClass().getSimpleName(),
            id
        );
        List<Event> events = store.get(id);
        if (events == null) {
            events = new ArrayList<>();
            events.add(event);
            store.put(id, events);
        } else {
            events.add(event);
        }

        Runnable runnable = () -> {
            for (Observer observer : observerList) {
                observer.update(event);
            }
        };
        System.out.println("[WRITE] EventStore - notifying observers in separate Thread");
        Thread t = new Thread(runnable);
        t.start();

    }

    public List<Event> getEvents(UUID id) {
        return store.get(id);
    }

    @Override
    public void subscribe(Observer observer) {
        System.out.println("[WRITE] EventStore - new subscriber");
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    @Override
    public void unsubscribe(Observer observer) {
        observerList.remove(observer);
    }
}
