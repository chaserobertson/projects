package edu.byu.cs.superasteroids.model;

import java.util.Iterator;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class LevelAsteroidType {
    public int number;
    public AsteroidType asteroidType;

    public LevelAsteroidType(int number, int asteroidId) {
        this.number = number;

        int i = 1;
        for(AsteroidType AT : Model.SINGLETON.asteroidTypes) {
            if(asteroidId == i) asteroidType = AT;
            i++;
        }
    }
}
