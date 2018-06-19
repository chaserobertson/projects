package edu.byu.cs.superasteroids.model;
import android.graphics.RectF;
import android.graphics.PointF;

import edu.byu.cs.superasteroids.core.GraphicsUtils;
import java.util.Random;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class Asteroid extends MovingObject {
    public AsteroidType asteroidType = null;
    public RectF boundaryBox = null;
    public PointF speed;
    public int hits = 0;

    public Asteroid(AsteroidType asteroidType, PointF position, PointF speed) {
        this.asteroidType = asteroidType;
        this.imageID = asteroidType.imageID;
        this.position = position;
        this.speed = speed;

        float left = position.x - ((asteroidType.imageWidth / 2) * scaleX);
        float right = left + (asteroidType.imageWidth * scaleX);
        float top = position.y - ((asteroidType.imageHeight / 2) * scaleY);
        float bottom = top + (asteroidType.imageHeight * scaleY);
        boundaryBox = new RectF(left, top, right, bottom);
    }

    public void update(PointF position) {
        this.position = position;
        float left = position.x - ((asteroidType.imageWidth / 2) * scaleX);
        float right = left + (asteroidType.imageWidth * scaleX);
        float top = position.y - ((asteroidType.imageHeight / 2) * scaleY);
        float bottom = top + (asteroidType.imageHeight * scaleY);
        boundaryBox = new RectF(left, top, right, bottom);
    }

    public void shrink(int scale) {
        this.scaleX /= scale;
        this.scaleY /= scale;

        float left = position.x - ((asteroidType.imageWidth / 2) * scaleX);
        float right = left + (asteroidType.imageWidth * scaleX);
        float top = position.y - ((asteroidType.imageHeight / 2) * scaleY);
        float bottom = top + (asteroidType.imageHeight * scaleY);
        boundaryBox = new RectF(left, top, right, bottom);
    }

    public Asteroid copy() {
        AsteroidType asteroidType = this.asteroidType;
        PointF position = this.position;
        PointF speed = this.speed;

        Asteroid copy = new Asteroid(asteroidType, position, speed);

        copy.imageID = this.imageID;
        Random random = new Random();
        copy.speed = GraphicsUtils.rotate(this.speed, Ship.SINGLETON.rotationDegrees * random.nextFloat());

        float left = this.position.x - ((this.asteroidType.imageWidth / 2) * this.scaleX);
        float right = left + (this.asteroidType.imageWidth * this.scaleX);
        float top = position.y - ((this.asteroidType.imageHeight / 2) * this.scaleY);
        float bottom = top + (this.asteroidType.imageHeight * this.scaleY);
        copy.boundaryBox = new RectF(left, top, right, bottom);

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Asteroid asteroid = (Asteroid) o;

        if (hits != asteroid.hits) return false;
        if (asteroidType != null ? !asteroidType.equals(asteroid.asteroidType) : asteroid.asteroidType != null)
            return false;
        if (boundaryBox != null ? !boundaryBox.equals(asteroid.boundaryBox) : asteroid.boundaryBox != null)
            return false;
        return speed != null ? speed.equals(asteroid.speed) : asteroid.speed == null;

    }

    @Override
    public int hashCode() {
        int result = asteroidType != null ? asteroidType.hashCode() : 0;
        result = 31 * result + (boundaryBox != null ? boundaryBox.hashCode() : 0);
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + hits;
        return result;
    }
}
