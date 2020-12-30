package at.fhv.dluvgo.hotel.read.cqrs.query;

import java.time.LocalDateTime;

public class GetBookingsQuery {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public GetBookingsQuery(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
