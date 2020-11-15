package at.fhv.sysarch.lab2.game;

import at.fhv.sysarch.lab2.physics.PhysicsEngine;
import at.fhv.sysarch.lab2.rendering.Renderer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final Renderer renderer;
    private final PhysicsEngine engine;
    private double xStart;
    private double yStart;

    public Game(Renderer renderer, PhysicsEngine engine) {
        this.renderer = renderer;
        this.engine = engine;
        this.initWorld();

        // Start world simulation stepping
        this.engine.begin(
                this.engine.getWorld().getStep(),
                this.engine.getWorld()
        );
    }

    public void onMousePressed(MouseEvent e) {
        this.xStart = e.getX();
        this.yStart = e.getY();

        this.renderer.setDrawingState(Renderer.CueDrawingState.PRESSED);
    }

    public void onMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        Point2D point = getCalculatedPoint(x, y);
        double length = getLength(point);
        point = point.normalize();

        Ball.WHITE.getBody().applyImpulse(new Vector2(point.getX(), point.getY()));

        // Init cue drawing
        this.renderer.setCueCoords(point.getX() * length, point.getY() * length);
        this.renderer.setDrawingState(Renderer.CueDrawingState.RELEASED);
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        Point2D point = getCalculatedPoint(x, y);
        double length = getLength(point);
        point = point.normalize();

        // Init cue drawing
        this.renderer.setCueCoords(point.getX() * length, point.getY() * length);
        this.renderer.setDrawingState(Renderer.CueDrawingState.DRAGGED);

    }

    /**
     * Calculates the point where the mouse is relative to the starting point.
     *
     * @param x The current x coordinate
     * @param y The current y coordinate
     *
     * @return {@link Point2D} containing the newly calculated coordinates
     */
    private Point2D getCalculatedPoint(double x, double y) {
        var deltaX = this.xStart - x;
        var deltaY = this.yStart - y;

        return new Point2D(deltaX, deltaY);
    }

    /**
     * Calculates the length of a stroke based on a {@link Point2D}.
     *
     * @param point The point to use for calculation
     *
     * @return The calculated length
     */
    private double getLength(Point2D point) {
        double length = (point.magnitude() / 10) / 4;
        // Artificially limit length
        if (length > 10) {
            length = 10;
        }

        return length;
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2 * Ball.Constants.RADIUS * 2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);
            renderer.addBall(b);

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }
        }
    }

    private void initWorld() {
        List<Ball> balls = new ArrayList<>();

        for (Ball b : Ball.values()) {
            this.engine.getWorld().addBody(b.getBody());
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
        }

        this.placeBalls(balls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);

        renderer.addBall(Ball.WHITE);

        Table table = new Table();
        renderer.setTable(table);
    }
}