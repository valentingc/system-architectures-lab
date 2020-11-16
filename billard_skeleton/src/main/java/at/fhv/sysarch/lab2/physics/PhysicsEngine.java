package at.fhv.sysarch.lab2.physics;

import at.fhv.sysarch.lab2.game.Ball;
import at.fhv.sysarch.lab2.game.Table;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Vector2;

/**
 * @author Valentin Goronjic
 * @author Dominic Luidold
 */
public class PhysicsEngine implements ContactListener {
    private final World world;

    public PhysicsEngine() {
        world = new World();
        world.setGravity(World.ZERO_GRAVITY);
        world.addListener(this);
    }

    public void addBodyFromGame(Body body) {
        world.addBody(body);
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

    @Override
    public boolean persist(PersistedContactPoint point) {
        Body body1 = point.getBody1();
        Body body2 = point.getBody2();

        if (point.isSensor()) {
            // TODO - Enough overlap?
            //  - how to use listeners?
            if (body1.getUserData() instanceof Ball) {
                // welt-koordinaten
                Vector2 ballPosition = body1.getTransform().getTranslation();

                // relativ zu dem, was der tisch ist -> pocket ist teil vom tisch
                Vector2 pocketPosition = body2.getTransform().getTranslation();
                Vector2 pocketCenter = point.getFixture2().getShape().getCenter();

                // welt-koordinaten
                Vector2 pocketInWorld = pocketPosition.add(pocketCenter);

                Vector2 difference = ballPosition.difference(pocketInWorld);
                double magnitudeDifference = difference.getMagnitude();// was für eine größenordnung
                // ist der
                if (magnitudeDifference <= 0.035) {
                    System.out.println("GELOCHT");
                }
                // unterschied
                // abstand kleiner als delta -> pocketed



                // wieso eigene klasse -> direkt game
                // notify listener here

                // body1 is ball
            } else {
                // body2 is ball
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
}
