package at.fhv.dluvgo.hotel.write.event;

import java.util.Date;
import java.util.UUID;

public abstract class Event {
    public final UUID id = UUID.randomUUID();
    public final Date created = new Date();

    @Override
    public String toString() {
        return "Event [" + id + "] of type [" + getClass().getSimpleName() + "] created at [" + created + "]";
    }
}
