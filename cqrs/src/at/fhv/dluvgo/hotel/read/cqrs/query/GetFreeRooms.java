package at.fhv.dluvgo.hotel.read.cqrs.query;

import java.time.LocalDateTime;

public class GetFreeRooms {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int requiredRoomCapacity;

    public GetFreeRooms(LocalDateTime startTime, LocalDateTime endTime, int requiredRoomCapacity) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiredRoomCapacity = requiredRoomCapacity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getRequiredRoomCapacity() {
        return requiredRoomCapacity;
    }
}
