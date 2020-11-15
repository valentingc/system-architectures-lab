package at.fhv.sysarch.lab2.physics;

import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;

/**
 * @author Valentin
 */
public class PhysicsEngine implements StepListener, ContactListener {
    private final World world;

    public PhysicsEngine() {
        world = new World();
        world.setGravity(World.ZERO_GRAVITY);  //-> not needed
        world.addListener(this);
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void begin(Step step, World world) {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                world.step(1);
            }
        };

        Timer timer = new Timer("Step updater");
        timer.schedule(task, 1000 / 60);
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

    @Override
    public void sensed(ContactPoint point) {
        System.out.println("sensed");
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
        return false;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return false;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }
}
