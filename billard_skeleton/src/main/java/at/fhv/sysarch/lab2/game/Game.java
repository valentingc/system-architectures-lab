package at.fhv.sysarch.lab2.game;

import at.fhv.sysarch.lab2.physics.BallPocketedListener;
import at.fhv.sysarch.lab2.physics.BallsCollisionListener;
import at.fhv.sysarch.lab2.physics.ObjectsRestListener;
import at.fhv.sysarch.lab2.physics.PhysicsEngine;
import at.fhv.sysarch.lab2.rendering.Renderer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.RaycastResult;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

public class Game implements BallsCollisionListener, BallPocketedListener, ObjectsRestListener {
    private final Renderer renderer;
    private final PhysicsEngine engine;
    private final Set<Ball> pocketedBalls = new HashSet<>();
    /* ## Mouse & Cue ## */
    private Point2D mousePressedScr;
    private Point2D mousePressedPh;
    private double mouseReleasedAtPhysicsX;
    private double mouseReleasedAtY;

    /* ## Game relevant ## */
    private Player currentPlayer = Player.PLAYER_ONE;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private int pocketedBallsInRound = 0;
    private boolean roundRunning = false;
    private boolean moveHandled = false;
    private boolean ballsMoving = false;
    private boolean foul = false;
    /* ## White ball ## */
    private boolean whiteBallPocketed = false;
    private boolean whiteBallTouchedOtherBall = false;
    private boolean didNotStrokeWhiteBall = false;
    private Vector2 whiteBallPositionPreFoul;
    private Table table;

    public Game(Renderer renderer, PhysicsEngine engine) {
        this.renderer = renderer;
        this.engine = engine;
        this.initWorld();
        engine.setBallsCollisionListener(this);
        engine.setBallPocketedListener(this);
        engine.setObjectsRestListener(this);
    }

    public void onMousePressed(MouseEvent e) {
        if (ballsMoving) {
            return;
        }

        double x = e.getX(); // screen koordinaten?
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);
        // 1. erster punkt
        // wie ermitteln wie die richtung des rays?

        mousePressedScr = new Point2D(x, y);
        mousePressedPh = new Point2D(pX, pY);
        this.renderer.setCueStartCoordinates(mousePressedScr.getX(), mousePressedScr.getY());
        this.renderer.setCueEndCoordinates(mousePressedScr.getX(), mousePressedScr.getY());
        this.renderer.setDrawingState(Renderer.CueDrawingState.PRESSED);
    }

    /* ###### Mouse & Cue related methods ###### */

    public void onMouseReleased(MouseEvent e) {
        if (ballsMoving) {
            return;
        }
        double x = e.getX();
        double y = e.getY();
        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

        // Drawing
        Point2D cueLength = calculateCueDistance(mousePressedScr,
            new Point2D(x,y));

        // RayCasting
        this.renderer.setDrawingState(Renderer.CueDrawingState.RELEASED);

        Vector2 start = new Vector2(this.mousePressedPh.getX(), this.mousePressedPh.getY());
        Vector2 end = new Vector2(pX, pY);
        Vector2 direction = end.difference(start).multiply(-1); // we draw in the opposite direction

        // start und end punkt: differenz bilden
        // startpunkt + richtung -> ray erzeugen und unten übergeben
        Ray ray = new Ray(start, direction); // erster vektor: start, zweiter: richtung
        List<RaycastResult> results = new ArrayList<>();

        this.engine.getWorld().raycast(ray, Ball.Constants.RADIUS * 2, false, false, results);
        if (!results.isEmpty()) {
            // wenn es eine kugel ist: applyForce
            Body body = results.get(0).getBody();
            if (body.getUserData() instanceof Ball) {
                Ball b = (Ball) body.getUserData();
                body.applyImpulse(direction.multiply(7));
                if (!b.isWhite()) {
                    this.foul = true;
                    this.didNotStrokeWhiteBall = true;
                }
            }
        }
    }

    public void setOnMouseDragged(MouseEvent e) {
        if (ballsMoving) {
            return;
        }

        double x = e.getX();
        double y = e.getY();
        // Init cue drawing
        Point2D cueLength = calculateCueDistance(mousePressedScr,
            new Point2D(x,y));

        Point2D newEnd = mousePressedScr.add(cueLength.multiply(20));
        renderer.setCueEndCoordinates(newEnd.getX(), newEnd.getY());
        renderer.setDrawingState(Renderer.CueDrawingState.DRAGGED);
    }

    private Point2D calculateCueDistance(Point2D start, Point2D end) {
        Point2D cueDistance = end.subtract(start);

        double cueLength = cueDistance.magnitude() / 10 / 2;
        if (cueLength > 10) {
            cueLength = 10;
        }

        cueDistance = cueDistance.normalize();
        return new Point2D(cueDistance.getX() * cueLength, cueDistance.getY() * cueLength);
    }

    private void placeBalls(List<Ball> balls, boolean ignoreTopSpot) {
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

            if (!engine.isGameBodyKnown(b.getBody())) {
                engine.addBodyFromGame(b.getBody());
                renderer.addBall(b);
            }

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }

            if (ignoreTopSpot && 1 == colSize) {
                return;
            }
        }
    }

    /* ###### Game methods ###### */

    private void initWorld() {
        List<Ball> balls = new ArrayList<>();

        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE) {
                continue;
            }

            balls.add(b);
        }

        this.placeBalls(balls, false);

        setWhiteBallToDefaultPosition();
        engine.addBodyFromGame(Ball.WHITE.getBody());
        renderer.addBall(Ball.WHITE);

        table = new Table();
        engine.addBodyFromGame(table.getBody());
        renderer.setTable(table);

        renderer.setStrikeMessage("Next strike: " + currentPlayer.getName());
    }

    @Override
    public void onBallsCollide(Ball b1, Ball b2) {
        if (whiteBallTouchedOtherBall || moveHandled) {
            return;
        }

        if ((b1.isWhite() && !b2.isWhite() || (!b1.isWhite() && b2.isWhite()))) {
            whiteBallTouchedOtherBall = true;
        }
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        // Prevent ball from spinning before removing
        b.getBody().setLinearVelocity(0, 0);

        if (b.isWhite()) {
            whiteBallPocketed = true;
        } else {
            pocketedBallsInRound++;
            updatePlayerScore(1);
            pocketedBalls.add(b);

            engine.removeBodyFromGame(b.getBody());
            renderer.removeBall(b);
        }

        // Return value not needed
        return true;
    }

    @Override
    public void onEndAllObjectsRest() {
        roundRunning = true;
        ballsMoving = true;
        moveHandled = false;
        whiteBallPocketed = false;
        clearMessages();
    }

    @Override
    public void onStartAllObjectsRest() {
        if (!roundRunning) {
            return;
        }
        if (whiteBallPocketed) {
            declareFoul("White ball has been pocketed");
            setWhiteBallToDefaultPosition();
        } else if (foul && didNotStrokeWhiteBall) {
            declareFoul("Another ball than white was stroke");
            setWhiteBallToPreFoulPosition();
        } else if (!whiteBallPocketed && !whiteBallTouchedOtherBall && 0 == pocketedBallsInRound) {
            declareFoul("White ball has not touched other balls");
            setWhiteBallToPreFoulPosition();
        } else if (!whiteBallPocketed && whiteBallTouchedOtherBall && 0 == pocketedBallsInRound) {
            switchPlayers();
        }
        if (foul) {
            switchPlayers();
        } else {
            whiteBallPositionPreFoul = Ball.WHITE.getBody().getTransform().getTranslation();
        }

        resetGameIfOnlyOneLeft();

        roundRunning = false;
        ballsMoving = false;
        moveHandled = true;
        foul = false;
        whiteBallTouchedOtherBall = false;
        didNotStrokeWhiteBall = false;
        pocketedBallsInRound = 0;
    }

    private void setWhiteBallToDefaultPosition() {
        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        Ball.WHITE.getBody().setLinearVelocity(0, 0);
        whiteBallPositionPreFoul = Ball.WHITE.getBody().getTransform().getTranslation();
    }

    /* ###### Helper methods ###### */

    private void setWhiteBallToPreFoulPosition() {
        Ball.WHITE.setPosition(whiteBallPositionPreFoul.x, whiteBallPositionPreFoul.y);
        Ball.WHITE.getBody().setLinearVelocity(0, 0);
        whiteBallPositionPreFoul = Ball.WHITE.getBody().getTransform().getTranslation();
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
        renderer.setStrikeMessage("Next strike: " + currentPlayer.getName());
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
        foul = true;

        renderer.setFoulMessage("Foul: " + message);
        updatePlayerScore(-1);
    }

    private void resetGameIfOnlyOneLeft() {
        if (pocketedBalls.size() >= 14) {
            List<Ball> balls = new ArrayList<>();

            for (Ball b : Ball.values()) {
                if (b == Ball.WHITE || !pocketedBalls.contains(b)) {
                    continue;
                }

                balls.add(b);
            }

            setWhiteBallToDefaultPosition();
            placeBalls(balls, true);
            table = new Table();
            engine.removeBodyFromGame(table.getBody());
            engine.addBodyFromGame(table.getBody());
            renderer.setTable(table);
            pocketedBalls.clear();
        }
    }

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
}