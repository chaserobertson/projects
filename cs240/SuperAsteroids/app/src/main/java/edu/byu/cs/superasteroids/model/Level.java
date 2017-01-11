package edu.byu.cs.superasteroids.model;

import java.util.ArrayList;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class Level {
    public int number;
    public String title;
    public String hint;
    public int width;
    public int height;
    public String music;
    public ArrayList<LevelBackgroundObject> levelBackgroundObjects;
    public ArrayList<LevelAsteroidType> levelAsteroidTypes;

    public Level(int number, String title, String hint, int width, int height, String music) {
        this.number = number;
        this.title = title;
        this.hint = hint;
        this.width = width;
        this.height = height;
        this.music = music;
    }
    public void addLevelBackgroundObjects(ArrayList<LevelBackgroundObject> levelBackgroundObjects) {
        this.levelBackgroundObjects = levelBackgroundObjects;
    }
    public void addLevelAsteroidType(ArrayList<LevelAsteroidType> levelAsteroidTypes) {
        this.levelAsteroidTypes = levelAsteroidTypes;
    }
}
