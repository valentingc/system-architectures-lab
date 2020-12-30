package at.fhv.dluvgo.hotel.write.aggregate;

import at.fhv.dluvgo.hotel.write.cqrs.command.CreateRoomCommand;
import at.fhv.dluvgo.hotel.write.event.Event;
import at.fhv.dluvgo.hotel.write.event.RoomCreatedEvent;
import at.fhv.dluvgo.hotel.write.repository.EventStore;
import java.util.Arrays;
import java.util.List;

public class RoomAggregate {
    private final EventStore eventStore;

    public RoomAggregate(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<Event> handleCreateRoomCommand(CreateRoomCommand command) {
        RoomCreatedEvent event = new RoomCreatedEvent(
            command.getRoomNumber(),
            command.getMaxPeople()
        );

        eventStore.addEvent(command.getRoomNumber(), event);

        return Arrays.asList(event);
    }
}
