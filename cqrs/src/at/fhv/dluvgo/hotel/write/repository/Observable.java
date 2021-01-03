package at.fhv.dluvgo.hotel.write.repository;

import at.fhv.dluvgo.hotel.read.projection.Observer;

public interface Observable {
    void subscribe(Observer observer);

    void unsubscribe(Observer observer);
}
