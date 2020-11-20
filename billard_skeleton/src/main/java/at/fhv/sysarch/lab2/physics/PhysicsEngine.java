package at.fhv.sysarch.lab2.physics;

import at.fhv.sysarch.lab2.game.Ball;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Vector2;

public class PhysicsEngine implements ContactListener {
    private final World world;
    private BallPocketedListener ballPocketedListener;
    private ObjectsRestListener objectsRestListener;

    public PhysicsEngine() {
        world = new World();
        world.setGravity(World.ZERO_GRAVITY);
        world.addListener(this);
    }

    public void addBodyFromGame(Body body) {
        world.addBody(body);
    }

    public void removeBodyFromGame(Body body) {
        world.removeBody(body);
    }

    public boolean isGameBodyKnown(Body body) {
        return world.getBodies().contains(body);
    }

    public void update(double deltaTime) {
        world.update(deltaTime);
    }

    public void setBallPocketedListener(BallPocketedListener ballPocketedListener) {
        this.ballPocketedListener = ballPocketedListener;
    }

    public void setObjectsRestListener(ObjectsRestListener objectsRestListener) {
        this.objectsRestListener = objectsRestListener;
    }

    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        return false;
    }

    @Override
    public void end(ContactPoint point) {

    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        if (point.isSensor()) {
            Body ball;
            Body pocket;

            if (point.getBody1().getUserData() instanceof Ball) {
                ball = point.getBody1();
                pocket = point.getBody2();
            } else {
                ball = point.getBody2();
                pocket = point.getBody1();
            }

            if (isBallPocketed(ball, pocket, point)) {
                ballPocketedListener.onBallPocketed((Ball) ball.getUserData());
            }
        }

        return true;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return true;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }

    // TODO - Pocket coordinates (0, 0) after first ever ball pocketed
    private boolean isBallPocketed(Body ball, Body pocket, PersistedContactPoint point) {
        // World coordinates of ball
        Vector2 ballPosition = ball.getTransform().getTranslation();

        // Pocket position (relative to table)
        Vector2 pocketPosition = pocket.getTransform().getTranslation();
        Vector2 pocketCenter = point.getFixture2().getShape().getCenter();

        // World coordinates of pocket
        Vector2 pocketInWorld = pocketPosition.add(pocketCenter);

        Vector2 difference = ballPosition.difference(pocketInWorld);
        double magnitudeDifference = difference.getMagnitude();

        return magnitudeDifference <= 0.035;
    }
}
