package at.fhv.dluvgo.hotel.read.projection;

import at.fhv.dluvgo.hotel.write.event.Event;

public interface Observer {
    void update(Event event);
}
