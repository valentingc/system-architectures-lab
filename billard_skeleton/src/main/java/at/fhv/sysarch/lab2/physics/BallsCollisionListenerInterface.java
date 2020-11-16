package at.fhv.sysarch.lab2.physics;

import at.fhv.sysarch.lab2.game.Ball;

public interface BallsCollisionListenerInterface {
    public void onBallsCollide(Ball b1, Ball b2);
}