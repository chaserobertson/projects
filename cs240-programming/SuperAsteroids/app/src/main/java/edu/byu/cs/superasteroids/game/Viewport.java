package edu.byu.cs.superasteroids.game;

import android.graphics.PointF;
import android.graphics.RectF;

import edu.byu.cs.superasteroids.core.GraphicsUtils;
import edu.byu.cs.superasteroids.drawing.DrawingHelper;
import edu.byu.cs.superasteroids.model.*;
/**
 * Created by chaserobertson on 5/24/16.
 */
public class Viewport {
    public static Viewport SINGLETON = new Viewport();
    public int viewWidth = 1024;
    public int viewHeight = 528;
    public int levelWidth;
    public int levelHeight;
    public PointF worldCoordinates;
    public PointF centerCoordinates;
    public RectF boundaryBox;
    public StarField starField;

    public void loadLevel(Level level) {
        starField = Model.SINGLETON.starField;
        this.levelWidth = level.width;
        this.levelHeight = level.height;
        this.centerCoordinates = new PointF(viewWidth / 2, viewHeight / 2);
        this.starField.position = new PointF(levelWidth / 2, levelWidth / 2);
        this.worldCoordinates = GraphicsUtils.subtract(starField.position, centerCoordinates);

        this.starField.scaleX = 1.4f;
        this.starField.scaleY = 1.4f;

        float left = worldCoordinates.x;
        float right = worldCoordinates.x + viewWidth;
        float top = worldCoordinates.y;
        float bottom = worldCoordinates.y + viewHeight;
        this.boundaryBox = new RectF(left, top, right, bottom);
    }

    public PointF toWorldCoordinates(PointF point) {
        return GraphicsUtils.add(point, worldCoordinates);
    }

    public PointF toViewportCoordinates(PointF point) {
        return GraphicsUtils.subtract(point, worldCoordinates);
    }

    public void adjustToShip(Ship ship) {
//            RectF right = new RectF(World.SINGLETON.boundaryBox.right - 1, World.SINGLETON.boundaryBox.top,
//                    World.SINGLETON.boundaryBox.right, World.SINGLETON.boundaryBox.bottom);
//            RectF left = new RectF(World.SINGLETON.boundaryBox.left, World.SINGLETON.boundaryBox.top,
//                    World.SINGLETON.boundaryBox.left + 1, World.SINGLETON.boundaryBox.bottom);
//            RectF top = new RectF(World.SINGLETON.boundaryBox.left, World.SINGLETON.boundaryBox.top,
//                    World.SINGLETON.boundaryBox.right, World.SINGLETON.boundaryBox.top + 1);
//            RectF bottom = new RectF(World.SINGLETON.boundaryBox.left, World.SINGLETON.boundaryBox.bottom - 1,
//                    World.SINGLETON.boundaryBox.right, World.SINGLETON.boundaryBox.bottom);
//            PointF adjustedSpeed = new PointF(0.0f, 0.0f);
//            if ( RectF.intersects(this.boundaryBox, right) || RectF.intersects(this.boundaryBox, left)) {
//                adjustedSpeed.y = ship.speed.y;
//            }
//            if ( RectF.intersects(this.boundaryBox, top) || RectF.intersects(this.boundaryBox, bottom) ) {
//                adjustedSpeed.x = ship.speed.x;
//            }
//            this.worldCoordinates = GraphicsUtils.add(worldCoordinates, adjustedSpeed);

        PointF viewStuff = new PointF (viewWidth / 2, viewHeight / 2);
        this.worldCoordinates = GraphicsUtils.subtract(ship.position, viewStuff);

        float left1 = worldCoordinates.x;
        float right1 = worldCoordinates.x + viewWidth;
        float top1 = worldCoordinates.y;
        float bottom1 = worldCoordinates.y + viewHeight;
        this.boundaryBox = new RectF(left1, top1, right1, bottom1);
    }
}
