package at.fhv.sysarch.lab2.game;

import at.fhv.sysarch.lab2.physics.BallPocketedListener;
import at.fhv.sysarch.lab2.physics.ObjectsRestListener;
import at.fhv.sysarch.lab2.physics.PhysicsEngine;
import at.fhv.sysarch.lab2.rendering.Renderer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game implements BallPocketedListener, ObjectsRestListener {
    public enum Player {
        PLAYER_ONE("Player 1"),
        PLAYER_TWO("Player 2");

        private final String name;

        Player(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final Renderer renderer;
    private final PhysicsEngine engine;

    /* ## Mouse & Cue ## */
    private double mousePressedAtX;
    private double mousePressedAtY;

    /* ## Game relevant ## */
    private Player currentPlayer;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    public Game(Renderer renderer, PhysicsEngine engine) {
        this.renderer = renderer;
        this.engine = engine;
        this.initWorld();

        engine.setBallPocketedListener(this);
        engine.setObjectsRestListener(this);
    }

    /* ###### Mouse & Cue related methods ###### */

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

        mousePressedAtX = x;
        mousePressedAtY = y;
        this.renderer.setDrawingState(Renderer.CueDrawingState.PRESSED);
    }

    public void onMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        Point2D relativeMousePoint = calculateRelativePointOfMouse(x, y);
        double cueLength = calculateCueLength(relativeMousePoint);
        relativeMousePoint = relativeMousePoint.normalize();

        // TODO - Refactor with RayCasting
        Ball.WHITE.getBody().applyImpulse(new Vector2(
                relativeMousePoint.getX() * cueLength,
                relativeMousePoint.getY() * cueLength
        ));

        // Init cue drawing
        renderer.setCueCoordinates(
                relativeMousePoint.getX() * cueLength,
                relativeMousePoint.getY() * cueLength
        );
        renderer.setDrawingState(Renderer.CueDrawingState.RELEASED);
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);

        Point2D relativeMousePoint = calculateRelativePointOfMouse(x, y);
        double cueLength = calculateCueLength(relativeMousePoint);
        relativeMousePoint = relativeMousePoint.normalize();

        // Init cue drawing
        renderer.setCueCoordinates(
                relativeMousePoint.getX() * cueLength,
                relativeMousePoint.getY() * cueLength
        );
        renderer.setDrawingState(Renderer.CueDrawingState.DRAGGED);
    }

    // TODO - Explanation for calculations
    private double calculateCueLength(Point2D point) {
        double length = (point.magnitude() / 10) / 4;
        // Artificially limit length
        if (length > 10) {
            length = 10;
        }

        return length;
    }

    private Point2D calculateRelativePointOfMouse(double x, double y) {
        double deltaX = mousePressedAtX - x;
        double deltaY = mousePressedAtY - y;

        return new Point2D(deltaX, deltaY);
    }

    /* ###### ###### */

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
            engine.addBodyFromGame(b.getBody());
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
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
        }

        this.placeBalls(balls);

        setWhiteBallToDefaultPosition();

        Table table = new Table();
        engine.addBodyFromGame(table.getBody());
        renderer.setTable(table);
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        // Prevent ball from spinning before removing
        b.getBody().setLinearVelocity(0, 0);

        engine.removeBodyFromGame(b.getBody());
        renderer.removeBall(b);

        if (b.isWhite()) {
            setWhiteBallToDefaultPosition();
        }

        // Return value not needed
        return false;
    }

    @Override
    public void onEndAllObjectsRest() {

    }

    @Override
    public void onStartAllObjectsRest() {

    }

    private void setWhiteBallToDefaultPosition() {
        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        if (!engine.isGameBodyKnown(Ball.WHITE.getBody())) {
            engine.addBodyFromGame(Ball.WHITE.getBody());
            renderer.addBall(Ball.WHITE);
        }
    }

    private void clearMessages() {
        renderer.setFoulMessage("");
        renderer.setActionMessage("");
    }

    private void switchPlayers() {
        if (currentPlayer.equals(Player.PLAYER_ONE)) {
            currentPlayer = Player.PLAYER_TWO;
        } else {
            currentPlayer = Player.PLAYER_ONE;
        }
        renderer.setActionMessage("Switching players, next player: " + currentPlayer.getName());
    }

    private void updatePlayerScore(int scoredPoint) {
        if (currentPlayer.equals(Player.PLAYER_ONE)) {
            scorePlayer1 += scoredPoint;
            renderer.setPlayer1Score(scorePlayer1);
        } else {
            scorePlayer2 += scoredPoint;
            renderer.setPlayer2Score(scorePlayer2);
        }
    }

    private void declareFoul(String message) {
        renderer.setFoulMessage("Foul: " + message);

        updatePlayerScore(-1);
        setWhiteBallToDefaultPosition();
        switchPlayers();
    }
}