package at.fhv.sysarch.lab2.game;

import at.fhv.sysarch.lab2.physics.PhysicsEngine;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab2.rendering.Renderer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.dyn4j.geometry.Vector2;

public class Game {
    private final Renderer renderer;
    private final PhysicsEngine engine;
    private double xStart;
    private double yStart;
    private double xEnd;
    private double yEnd;

    public Game(Renderer renderer, PhysicsEngine engine) {
        this.renderer = renderer;
        this.engine = engine;
        this.initWorld();
    }

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        System.out.println("Mouse pressed");
        this.xStart = x;
        this.yStart = y;
        this.xEnd = x;
        this.yEnd = y;
    }

    public void onMouseReleased(MouseEvent e) {
        System.out.println("Mouse relesed");
        double x = e.getX();
        double y = e.getY();

        Point2D point = getCalculatedPoint(x, y);
        var length = getLength(point);

        //Ball.WHITE.getBody().applyImpulse(new Vector2(point.getX(), point.getY()));
        // zeichnen
        this.renderer.setCueCoords(point.getX() * length, point.getY() * length);
        this.renderer.setDrawingState(Renderer.CueDrawingState.RELEASED);
    }

    public void setOnMouseDragged(MouseEvent e) {
        System.out.println("Mouse dragged");
        double x = e.getX();
        double y = e.getY();
        this.xEnd = x;
        this.yEnd = y;

        Point2D point = getCalculatedPoint(x, y);
        var length = getLength(point);

        // zeichnen
        this.renderer.setCueCoords(point.getX() * length, point.getY() * length);
        this.renderer.setDrawingState(Renderer.CueDrawingState.DRAGGED);

    }

    private Point2D getCalculatedPoint(double x, double y) {
        var deltaX = this.xStart - x;
        var deltaY = this.yStart - y;

        Point2D point = new Point2D(deltaX, deltaY);
        point = point.normalize();

        return point;
    }

    private double getLength(Point2D point) {
        var length = (point.magnitude() / 10) / 4;
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

        double y0 = -2*Ball.Constants.RADIUS*2;
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