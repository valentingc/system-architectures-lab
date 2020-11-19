package at.fhv.sysarch.lab2.physics;

import at.fhv.sysarch.lab2.game.Ball;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Vector2;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class PhysicsEngine implements ContactListener, StepListener {
    private final World world;
    private final List<BallPocketedListener> ballPocketListeners;
    private final List<ObjectsRestListener> objectRestListeners;

    public PhysicsEngine() {
        world = new World();
        world.setGravity(World.ZERO_GRAVITY);
        world.addListener(this);
        ballPocketListeners = new LinkedList<>();
        objectRestListeners = new LinkedList<>();
    }

    public void addBodyFromGame(Body body) {
        world.addBody(body);
    }

    public void removeBodyFromGame(Body body) {
        world.removeBody(body);
    }

    public void update(double deltaTime) {
        world.update(deltaTime);
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

    public void addBallPocketedListener(BallPocketedListener ballPocketedListener) {
        ballPocketListeners.add(ballPocketedListener);
    }

    public boolean removeBallPocketedListener(BallPocketedListener o) {
        return ballPocketListeners.remove(o);
    }

    public void addObjectRestListener(ObjectsRestListener objectRestListener) {
        objectRestListeners.add(objectRestListener);
    }

    public boolean removeObjectRestListener(ObjectsRestListener objectRestListener) {
        return objectRestListeners.remove(objectRestListener);
    }

    private boolean isAPocketedBall(Body ball, Body pocket, PersistedContactPoint point) {
        // World coordinates of ball
        Vector2 ballPosition = ball.getTransform().getTranslation();

        // relativ zu dem, was der tisch ist -> pocket ist teil vom tisch
        Vector2 pocketPosition = pocket.getTransform().getTranslation();
        Vector2 pocketCenter = point.getFixture2().getShape().getCenter();

        // World coordinates of pocket
        Vector2 pocketInWorld = pocketPosition.add(pocketCenter);

        Vector2 difference = ballPosition.difference(pocketInWorld);
        double magnitudeDifference = difference.getMagnitude();// was für eine größenordnung
        // ist der unterschied
        // abstand kleiner als delta -> pocketed
        return magnitudeDifference <= 0.035;
    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        Body body1 = point.getBody1();
        Body body2 = point.getBody2();

        if (point.isSensor()) {
            // TODO - Enough overlap?
            //  - how to use listeners?
            if (body1.getUserData() instanceof Ball) {
                // body1 is ball
                Ball b = (Ball) body1.getUserData();
                boolean isPocketed = isAPocketedBall(body1, body2, point);

                if (isPocketed) {
                    System.out.println("11111Yep, ball is pocketed");
                    System.out.println(b.getColor().toString());
                }
                for (BallPocketedListener listener : ballPocketListeners) {
                    listener.onBallPocketed(b);
                }

                // wieso eigene klasse -> direkt game
                // notify listener here

            } else if (body2.getUserData() instanceof Ball) {
                Ball b = (Ball) body2.getUserData();
                // body2 is ball
                boolean isPocketed = isAPocketedBall(body2, body1, point);

                if (isPocketed) {
                    System.out.println("22222Yep, ball is pocketed");
                    System.out.println(b.getColor().toString());
                }
                for (BallPocketedListener listener : ballPocketListeners) {
                    listener.onBallPocketed(b);
                }
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

    @Override
    public void begin(Step step, World world) {
        boolean areBallsMoving = false;
        for (Ball b : Ball.values()) {
            if (!b.getBody().getLinearVelocity().equals(new Vector2(0, 0))) {
                areBallsMoving = true;
                break;
            }
        }
        if (areBallsMoving) {
            for (ObjectsRestListener listener : objectRestListeners) {
                listener.allObjectsMoving();
            }
        } else {
            for (ObjectsRestListener listener : objectRestListeners) {
                listener.allObjectsRest();
            }
        }
    }

    @Override
    public void updatePerformed(Step step, World world) {

    }

    @Override
    public void postSolve(Step step, World world) {

    }

    @Override
    public void end(Step step, World world) {

    }
}
