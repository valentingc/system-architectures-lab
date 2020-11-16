package at.fhv.sysarch.lab2.physics;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;

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
