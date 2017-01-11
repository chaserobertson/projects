package edu.byu.cs.superasteroids.model;

import android.graphics.PointF;
/**
 * Created by chaserobertson on 5/16/16.
 */
public class VisibleObject {
    public int imageID = -1;
    public PointF position = new PointF(0f, 0f);
    public float rotationDegrees = 0f;
    public float scaleX = 0.3f;
    public float scaleY = 0.3f;
    public int alpha = 255;

    /**
     * updates location values according to past position and velocity
     */
    public void update() {}

}
