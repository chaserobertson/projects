package edu.byu.cs.superasteroids.model;

import android.graphics.RectF;
import android.graphics.PointF;

import edu.byu.cs.superasteroids.core.GraphicsUtils;
import edu.byu.cs.superasteroids.drawing.DrawingHelper;
import edu.byu.cs.superasteroids.game.Viewport;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class Projectile extends MovingObject {
    public String image;
    public int imageWidth;
    public int imageHeight;
    public Cannon cannon;
    public RectF boundaryBox = null;
    public PointF position;
    public PointF speed;

//    public Projectile(String image, int imageId) {
//        this.image = image;
//        this.imageID = imageId;
//    }

    public Projectile(Cannon cannon) {
        this.image = cannon.attackImage;
        this.imageWidth = cannon.attackImageWidth;
        this.imageHeight = cannon.attackImageHeight;
        this.imageID = cannon.attackImageId;
        this.cannon = cannon;
        this.speed = GraphicsUtils.scale(Ship.SINGLETON.speed, 4);
        Ship ship = Ship.SINGLETON;
        Viewport viewport = Viewport.SINGLETON;

        if(ship.isInitialized()) {
            this.rotationDegrees = ship.rotationDegrees;

            PointF cannonCenter = new PointF((cannon.imageWidth / 2), (cannon.imageHeight / 2));
            PointF cannonOffset = GraphicsUtils.subtract( cannon.emitPoint, cannonCenter);
            cannonOffset = GraphicsUtils.scale(cannonOffset, scaleX);
            cannonOffset = GraphicsUtils.rotate(cannonOffset, GraphicsUtils.degreesToRadians(ship.rotationDegrees));
            PointF emitPoint = GraphicsUtils.add(cannon.position, cannonOffset);
            this.position = emitPoint;

            float right = position.x + ((imageWidth / 2) * scaleX);
            float bottom = position.y + ((imageHeight / 2) * scaleY);
            float top = bottom - (imageHeight * scaleY);
            float left = right - (imageWidth * scaleX);
            this.boundaryBox = new RectF(left, top, right, bottom);
        }
    }

    public void update(PointF position) {
        this.position = position;

        float right = position.x + ((imageWidth / 2) * scaleX);
        float left = right - (imageWidth * scaleX);
        float bottom = position.y + ((imageHeight / 2) * scaleY);
        float top = bottom - (imageHeight * scaleY);
        this.boundaryBox = new RectF(left, top, right, bottom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Projectile that = (Projectile) o;

        if (imageWidth != that.imageWidth) return false;
        if (imageHeight != that.imageHeight) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (cannon != null ? !cannon.equals(that.cannon) : that.cannon != null) return false;
        if (boundaryBox != null ? !boundaryBox.equals(that.boundaryBox) : that.boundaryBox != null)
            return false;
        if (position != null ? !position.equals(that.position) : that.position != null)
            return false;
        return speed != null ? speed.equals(that.speed) : that.speed == null;

    }

    @Override
    public int hashCode() {
        int result = image != null ? image.hashCode() : 0;
        result = 31 * result + imageWidth;
        result = 31 * result + imageHeight;
        result = 31 * result + (cannon != null ? cannon.hashCode() : 0);
        result = 31 * result + (boundaryBox != null ? boundaryBox.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        return result;
    }
}
