package edu.byu.cs.superasteroids.game;

import android.app.admin.DeviceAdminInfo;
import android.bluetooth.BluetoothClass;

import edu.byu.cs.superasteroids.R;
import edu.byu.cs.superasteroids.base.IGameDelegate;
import edu.byu.cs.superasteroids.content.ContentManager;
import edu.byu.cs.superasteroids.core.GraphicsUtils;
import edu.byu.cs.superasteroids.drawing.DrawingHelper;
import edu.byu.cs.superasteroids.model.*;
import edu.byu.cs.superasteroids.ship_builder.ShipBuilderShipView;
import android.graphics.PointF;
import android.graphics.RectF;

import org.apache.http.client.RedirectException;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by chaserobertson on 5/22/16.
 */
public class GameDelegate implements IGameDelegate {
    public Model model = Model.SINGLETON;
    private Ship ship = Ship.SINGLETON;
    private Viewport viewport = Viewport.SINGLETON;
    public World world = World.SINGLETON;
    public Level level = model.levels.iterator().next();
    public ArrayList<LevelBackgroundObject> backgroundObjects = new ArrayList<>();
    public ArrayList<Asteroid> asteroids = new ArrayList<>();
    public ArrayList<Asteroid> asteroidsRemoval = new ArrayList<>();
    public ArrayList<Asteroid> asteroidsAdd = new ArrayList<>();
    public ArrayList<Projectile> projectiles = new ArrayList<>();
    public ArrayList<Projectile> projectilesRemoval = new ArrayList<>();
    private int count = 15;
    private int shipLives = 50;
    private int asteroidHits = 0;
    private int safeCount = 300;
    private boolean fireProjectile = false;

    public GameDelegate() {
        viewport.loadLevel(level);
        world.setBoundary(level.width, level.height);
        ship.onCreate();

        for(int i = 0; i < level.levelBackgroundObjects.size(); i++) {
            backgroundObjects.add(level.levelBackgroundObjects.get(i));
        }
        for(int i = 0; i < level.levelAsteroidTypes.size(); i++) {
            for (int j = 0; j < level.levelAsteroidTypes.get(i).number; j++) {
                Asteroid asteroid;
                while(true) {
                    PointF position = randomAsteroidPoint();
                    PointF speed = randomAsteroidSpeed();
                    asteroid = new Asteroid(level.levelAsteroidTypes.get(i).asteroidType, position, speed);
                    if ( !RectF.intersects(asteroid.boundaryBox, viewport.boundaryBox) &&
                            !viewport.boundaryBox.contains(asteroid.boundaryBox)) break;
                }
                asteroids.add(asteroid);
            }
        }
    }

    @Override
    public void update(double elapsedTime) {
        if(shipLives < 1) endGame();

        PointF newShipPosition = GraphicsUtils.add(ship.position, ship.speed);
        ship.update(newShipPosition);

        PointF movePoint = InputManager.movePoint;
        if(movePoint != null) {
            PointF viewportMovePoint = GraphicsUtils.subtract(movePoint, viewport.centerCoordinates);
            ship.speed = GraphicsUtils.scale(viewportMovePoint, 0.03f);
            ship.rotationDegrees = (float)GraphicsUtils.radiansToDegrees(angle(ship.speed)) + 90.0f;
        }

        if(count < 15) count++;
        if(InputManager.firePressed) {
            if(count >= 15) {
                fireProjectile = true;
                count = 0;
            }
        }

        RectF right = new RectF(world.boundaryBox.right - 1, world.boundaryBox.top,
                world.boundaryBox.right, world.boundaryBox.bottom);
        RectF left = new RectF(world.boundaryBox.left, world.boundaryBox.top,
                world.boundaryBox.left + 1, world.boundaryBox.bottom);
        RectF top = new RectF(world.boundaryBox.left, world.boundaryBox.top,
                world.boundaryBox.right, world.boundaryBox.top + 1);
        RectF bottom = new RectF(world.boundaryBox.left, world.boundaryBox.bottom - 1,
                world.boundaryBox.right, world.boundaryBox.bottom);
        if ( RectF.intersects(ship.boundaryBox, right) || RectF.intersects(ship.boundaryBox, left)) {
            ship.speed.x *= -1;
            ship.rotationDegrees = (float)GraphicsUtils.radiansToDegrees(angle(ship.speed)) + 90 ;
        }
        if ( RectF.intersects(ship.boundaryBox, top) || RectF.intersects(ship.boundaryBox, bottom) ) {
            ship.speed.y *= -1;
            ship.rotationDegrees = (float)GraphicsUtils.radiansToDegrees(angle(ship.speed)) + 90 ;

        }
        viewport.adjustToShip(ship);

        checkAsteroids();
        checkProjectiles();
    }

    private void checkProjectiles() {
        for(Projectile current : projectiles) {
            PointF newPosition = GraphicsUtils.add(current.position, current.speed);
            current.update(newPosition);

            //check for projectile-world edge collision
            if ( current.boundaryBox.left <= world.boundaryBox.left || current.boundaryBox.right >= world.boundaryBox.right
                    || current.boundaryBox.top <= world.boundaryBox.top || current.boundaryBox.bottom >= world.boundaryBox.bottom) {
                projectilesRemoval.add(current);
            }

            //check for asteroid-projectile collision
            for(Asteroid currentAsteroid : asteroids) {
                if(RectF.intersects(current.boundaryBox, currentAsteroid.boundaryBox)) {
                    projectilesRemoval.add(current);
                    asteroidHit(currentAsteroid);
                }
            }
        }
        projectiles.removeAll(projectilesRemoval);
        asteroids.removeAll(asteroidsRemoval);
        asteroids.addAll(asteroidsAdd);
        asteroidsAdd.clear();
        projectilesRemoval.clear();
        asteroidsRemoval.clear();
    }

    private void checkAsteroids() {
        for(Asteroid current : asteroids) {
            PointF newAsteroidPosition = GraphicsUtils.add(current.position, current.speed);
            current.update(newAsteroidPosition);
            if(safeCount < 300) {
                ship.alpha = 255 / 2;
                safeCount++;
            }
            else ship.alpha = 255;

            //check for ship-asteroid collision
            if(RectF.intersects(current.boundaryBox, ship.boundaryBox)) {
                if(safeCount >= 300) {
                    safeCount = 0;
                    shipLives--;
                    asteroidHit(current);
                }
            }

            //check for asteroid-edge collision
            if ( current.boundaryBox.right >= World.SINGLETON.boundaryBox.right ||
                    current.boundaryBox.left <= World.SINGLETON.boundaryBox.left) {
                current.speed.x = current.speed.x * -1;
            }
            if (current.boundaryBox.top <= World.SINGLETON.boundaryBox.top ||
                    current.boundaryBox.bottom >= World.SINGLETON.boundaryBox.bottom) {
                current.speed.y = current.speed.y * -1;
            }

        }
        asteroids.removeAll(asteroidsRemoval);
        asteroidsRemoval.clear();
        asteroids.addAll(asteroidsAdd);
        asteroidsAdd.clear();
    }

    private void asteroidHit(Asteroid asteroid) {

//        if (asteroid.hits < 2) {
//            asteroid.hits++;
//            if (asteroid.asteroidType.name.equals("regular")) {
//                asteroid.shrink(2);
//                asteroidsAdd.add(asteroid.copy());
//                asteroid.speed = GraphicsUtils.rotate(asteroid.speed, Ship.SINGLETON.rotationDegrees * 2);
//                return;
//            } else if (asteroid.asteroidType.name.equals("growing")) {
//                asteroid.shrink(2);
//                asteroidsAdd.add(asteroid.copy());
//                asteroid.speed = GraphicsUtils.rotate(asteroid.speed, Ship.SINGLETON.rotationDegrees * 2);
//                return;
//            } else if (asteroid.asteroidType.name.equals("octeroid")) {
//                if(asteroid.hits < 2) {
//                    asteroid.shrink(8);
//                    asteroidsAdd.add(asteroid.copy());
//                    asteroid.speed = GraphicsUtils.rotate(asteroid.speed, Ship.SINGLETON.rotationDegrees * 2);
//                    return;
//                }
//            }
//        }

        asteroidsRemoval.add(asteroid);
    }

    private void fireProjectile() {
        Projectile projectile = new Projectile(ship.cannon);
        projectiles.add(projectile);
        fireProjectile = false;
    }

    @Override
    public void draw() {

        PointF starFieldPoint = viewport.toViewportCoordinates(viewport.starField.position);
        DrawingHelper.drawImage(viewport.starField.imageID, starFieldPoint.x, starFieldPoint.y, 0,
                viewport.starField.scaleX, viewport.starField.scaleY, viewport.starField.alpha);

        for(LevelBackgroundObject current : backgroundObjects) {
            PointF viewCoordinates = viewport.toViewportCoordinates(current.position);
            DrawingHelper.drawImage(current.backgroundObject.imageID, viewCoordinates.x, viewCoordinates.y,
                    current.rotationDegrees, current.scaleX, current.scaleY, current.alpha);
        }

        for(Asteroid current : asteroids) {
            PointF viewCoordinates = viewport.toViewportCoordinates(current.position);
            DrawingHelper.drawImage(current.imageID, viewCoordinates.x, viewCoordinates.y,
                    current.rotationDegrees, current.scaleX, current.scaleY, current.alpha);
        }

        drawShip();
        if(fireProjectile) fireProjectile();

        for(Projectile current : projectiles) {
            PointF viewCoordinates = viewport.toViewportCoordinates(current.position);
            DrawingHelper.drawImage(current.imageID, viewCoordinates.x, viewCoordinates.y,
                    current.rotationDegrees, current.scaleX, current.scaleY, current.alpha);
        }

        //ShipBuilderShipView.draw();
    }

    public void drawShip() {
        if(ship.mainBody != null) {
            ship.mainBody.position = ship.position;
            PointF mainBodyCoordinate = ship.position;
            PointF mainBodyViewCoordinate = viewport.toViewportCoordinates(mainBodyCoordinate);
            DrawingHelper.drawImage(ship.mainBody.imageID, mainBodyViewCoordinate.x, mainBodyViewCoordinate.y,
                    ship.rotationDegrees, ship.scaleX, ship.scaleY, ship.alpha);

            if (ship.cannon != null) {
                float cannonOffsetX = (ship.mainBody.imageWidth / 2) + (ship.cannon.imageWidth / 2);
                cannonOffsetX -= (ship.cannon.attachPoint.x + (ship.mainBody.imageWidth - ship.mainBody.cannonAttach.x));
                float cannonOffsetY = (ship.mainBody.imageHeight / 2) + (ship.cannon.imageHeight / 2);
                cannonOffsetY -= (ship.cannon.attachPoint.y + (ship.mainBody.imageHeight - ship.mainBody.cannonAttach.y));
                cannonOffsetX *= ship.scaleX;
                cannonOffsetY *= ship.scaleY;
                PointF cannonPoint = new PointF(cannonOffsetX, cannonOffsetY);
                cannonPoint = GraphicsUtils.rotate(cannonPoint, GraphicsUtils.degreesToRadians(ship.rotationDegrees));
                cannonPoint = GraphicsUtils.add(mainBodyCoordinate, cannonPoint);
                ship.cannon.position = cannonPoint;
                cannonPoint = viewport.toViewportCoordinates(cannonPoint);
                DrawingHelper.drawImage(ship.cannon.imageID, cannonPoint.x, cannonPoint.y, ship.rotationDegrees,
                        ship.scaleX, ship.scaleY, ship.alpha);
            }

            if (ship.extraParts != null) {
                float offsetX = (ship.mainBody.imageWidth / 2) + (ship.extraParts.imageWidth / 2);
                offsetX -= ((ship.extraParts.imageWidth - ship.extraParts.attachPoint.x) + ship.mainBody.extraAttach.x);
                float offsetY = (ship.mainBody.imageHeight / 2) + (ship.extraParts.imageHeight / 2);
                offsetY -= ((ship.extraParts.imageHeight - ship.extraParts.attachPoint.y) + ship.mainBody.extraAttach.y);
                offsetX *= ship.scaleX;
                offsetY *= ship.scaleY;
                PointF exPartPoint = new PointF(offsetX, offsetY);
                exPartPoint = GraphicsUtils.rotate(exPartPoint, GraphicsUtils.degreesToRadians(ship.rotationDegrees));
                exPartPoint = GraphicsUtils.subtract(mainBodyCoordinate, exPartPoint);
                ship.extraParts.position = exPartPoint;
                exPartPoint = viewport.toViewportCoordinates(exPartPoint);
                DrawingHelper.drawImage(ship.extraParts.imageID, exPartPoint.x, exPartPoint.y, ship.rotationDegrees,
                        ship.scaleX, ship.scaleY, ship.alpha);
            }

            if (ship.engine != null) {
                float offsetY = (ship.mainBody.imageHeight / 2) + (ship.engine.imageHeight / 2);
                offsetY -= (ship.engine.attachPoint.y + (ship.mainBody.imageHeight - ship.mainBody.engineAttach.y));
                offsetY *= ship.scaleY;
                PointF enginePoint = new PointF(0, offsetY);
                enginePoint = GraphicsUtils.rotate(enginePoint, GraphicsUtils.degreesToRadians(ship.rotationDegrees));
                enginePoint = GraphicsUtils.add(mainBodyCoordinate, enginePoint);
                ship.engine.position = enginePoint;
                enginePoint = viewport.toViewportCoordinates(enginePoint);
                DrawingHelper.drawImage(ship.engine.imageID, enginePoint.x, enginePoint.y, ship.rotationDegrees,
                        ship.scaleX, ship.scaleY, ship.alpha);
            }
        }
    }

    private PointF randomAsteroidPoint() {
        Random random = new Random();
        float randomX = random.nextFloat() * level.width;
        float randomY = random.nextFloat() * level.height;
        PointF point = new PointF(randomX, randomY);

        return point;
    }

    private PointF randomAsteroidSpeed() {
        Random random = new Random();
        float randomX = random.nextFloat() * MAX_SPEED;
        float randomY = random.nextFloat() * MAX_SPEED;
        if(random.nextBoolean()) randomX *= -1;
        if(random.nextBoolean()) randomY *= -1;
        PointF speed = new PointF(randomX, randomY);

        return speed;
    }

    private double angle(PointF dydx) {
        float y = dydx.y;
        float x = dydx.x;
        return Math.atan2(y,x);
    }

    @Override
    public void loadContent(ContentManager content) {}

    @Override
    public void unloadContent(ContentManager content) {}

    private void endGame() {
        while(true) {}
    }

    private static final int MAX_SPEED = 2;
}
