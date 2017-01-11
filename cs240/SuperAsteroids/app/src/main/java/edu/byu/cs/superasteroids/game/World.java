package edu.byu.cs.superasteroids.game;

import android.graphics.RectF;

/**
 * Created by chaserobertson on 5/25/16.
 */
public class World {
    public static World SINGLETON = new World();
    public int levelWidth;
    public int levelHeight;
    public RectF boundaryBox;

    public void setBoundary(int levelWidth, int levelHeight) {
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        boundaryBox = new RectF(0, 0, levelWidth, levelHeight);
    }
}
