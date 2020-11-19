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
    public enum CurrentPlayer {
        PLAYER_ONE("Player 1"),
        PLAYER_TWO("Player 2");
        private String prettyName;

        CurrentPlayer(String prettyName) {
            this.prettyName = prettyName;
        }

        public String getPrettyName() {
            return prettyName;
        }
    }

    private final Renderer renderer;
    private final PhysicsEngine engine;
    private double xStart;
    private double yStart;
    private CurrentPlayer currentPlayer;

    /* Player scores */
    private int player1Score = 0;
    private int player2Score = 0;

    private boolean ballsMoving = false;
    private boolean hasRoundStarted = false;
    private boolean gameHandledMove = false;
    private boolean hasWhiteBallBeenPocketed = false;
    private boolean hasNonWhiteBallBeenPocketed = false;
    private boolean hasWhiteBallTouchedOtherBalls = false;

    private Vector2 whiteBallPositionBeforeFoul;

    public Game(Renderer renderer, PhysicsEngine engine) {
        this.renderer = renderer;
        this.engine = engine;
        this.initWorld();
        this.engine.addBallPocketedListener(this);
        this.engine.addObjectRestListener(this);
    }

    public void onMousePressed(MouseEvent e) {
        if (ballsMoving) {
            return;
        }

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

        if (!ballsMoving) {
            Ball.WHITE.getBody().applyImpulse(new Vector2(point.getX() * length, point.getY() * length));
        }

        // Init cue drawing
        this.renderer.setCueCoords(point.getX() * length, point.getY() * length);
        this.renderer.setDrawingState(Renderer.CueDrawingState.RELEASED);
    }

    public void setOnMouseDragged(MouseEvent e) {
        if (ballsMoving) {
            return;
        }

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

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        whiteBallPositionBeforeFoul = Ball.WHITE.getBody().getTransform().getTranslation();

        // Add white ball
        engine.addBodyFromGame(Ball.WHITE.getBody());
        renderer.addBall(Ball.WHITE);

        Table table = new Table();
        engine.addBodyFromGame(table.getBody());
        renderer.setTable(table);

        // set current player
        this.currentPlayer = CurrentPlayer.PLAYER_ONE;
    }

    private void switchPlayers() {
        hasRoundStarted = false;
        gameHandledMove = false;

        if (this.currentPlayer.equals(CurrentPlayer.PLAYER_ONE)) {
            this.currentPlayer = CurrentPlayer.PLAYER_TWO;
        } else {
            this.currentPlayer = CurrentPlayer.PLAYER_ONE;
        }
        this.renderer.setActionMessage("Switching Players, next player: " + this.currentPlayer.getPrettyName());
    }

    private void resetWhiteBall() {
        Ball.WHITE.getBody().setLinearVelocity(0,0);
        Ball.WHITE.setPosition(whiteBallPositionBeforeFoul.x, whiteBallPositionBeforeFoul.y);
        if (hasWhiteBallBeenPocketed) {
            engine.addBodyFromGame(Ball.WHITE.getBody());
            renderer.addBall(Ball.WHITE);
        }
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        System.out.println("onBallPocketed called");
        b.getBody().setLinearVelocity(0, 0); // fixes a problem that the ball never stops.

        this.renderer.removeBall(b);
        this.engine.removeBodyFromGame(b.getBody());

        if (b.isWhite()) {
            hasWhiteBallBeenPocketed = true;
            hasNonWhiteBallBeenPocketed = false;
            this.declareFoul("White ball has been pocketed");
        } else {
            hasWhiteBallBeenPocketed = false;
            hasNonWhiteBallBeenPocketed = true;
            this.updatePoints(1);
        }

        return false;
    }

    @Override
    public void allObjectsMoving() {
        this.clearMessages();
        ballsMoving = true;
        hasRoundStarted = true;
        hasWhiteBallTouchedOtherBalls = false;

        for (Ball b : Ball.values()) {
            if (!b.isWhite() && !b.getBody().getLinearVelocity().equals(new Vector2(0, 0))) {
                hasWhiteBallTouchedOtherBalls = true;
                break;
            }
        }
    }

    @Override
    public void allObjectsRest() {
        if (hasRoundStarted && !gameHandledMove && (!hasNonWhiteBallBeenPocketed && !hasWhiteBallTouchedOtherBalls)) {
            gameHandledMove = true;
            this.declareFoul("White ball hasn't touched any other balls!");
        } else if (hasRoundStarted && !gameHandledMove && !hasNonWhiteBallBeenPocketed) {
            gameHandledMove = true;
            this.switchPlayers();
        }

        ballsMoving = false;
        whiteBallPositionBeforeFoul = Ball.WHITE.getBody().getTransform().getTranslation();
    }

    private void declareFoul(String message) {
        this.renderer.setFoulMessage("Foul: " + message);

        this.updatePoints(-1);
        this.resetWhiteBall();
        this.switchPlayers();
    }

    private void updatePoints(int point) {
        if (currentPlayer.equals(CurrentPlayer.PLAYER_ONE)) {
            player1Score += point;
            renderer.setPlayer1Score(player1Score);
        } else {
            player2Score += point;
            renderer.setPlayer2Score(player2Score);
        }
    }

    private void clearMessages() {
        renderer.setFoulMessage("");
        renderer.setActionMessage("");
    }
}