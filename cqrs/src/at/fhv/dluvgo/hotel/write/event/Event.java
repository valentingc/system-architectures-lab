package at.fhv.dluvgo.hotel.write.event;

import java.util.Date;
import java.util.UUID;

public abstract class Event {
    private final UUID id = UUID.randomUUID();
    private final Date created = new Date();

    public UUID getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "Event [" + id + "] of type [" + getClass().getSimpleName() + "] created at [" + created + "]";
    }
}
