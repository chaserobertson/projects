package edu.byu.cs.superasteroids.model;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.superasteroids.content.ContentManager;

/**
 * Created by chaserobertson on 5/16/16.
 *
 * Container for all other classes/objects in this package
 */
public class Model {
    public static final Model SINGLETON = new Model();
    public ArrayList<VisibleObject> content = null;

    public ArrayList<BackgroundObject> backgroundObjects = null;
    public ArrayList<AsteroidType> asteroidTypes = null;
    public ArrayList<Level> levels = null;
    public ArrayList<MainBody> mainBodies = null;
    public ArrayList<Cannon> cannons = null;
    public ArrayList<ExtraParts> extraPartses = null;
    public ArrayList<Engine> engines = null;
    public ArrayList<PowerCore> powerCores = null;
    public StarField starField = null;

    public boolean isEmpty() {
        boolean empty = false;

        if(mainBodies == null || mainBodies.isEmpty()) empty = true;
        if(extraPartses == null || extraPartses.isEmpty()) empty = true;
        if(cannons == null || cannons.isEmpty()) empty = true;
        if(engines == null || engines.isEmpty()) empty = true;
        if(powerCores == null || powerCores.isEmpty()) empty = true;

        return empty;
    }

    public void clear() {
        content = null;
        starField = null;
        backgroundObjects = null;
        asteroidTypes = null;
        levels = null;
        mainBodies = null;
        cannons = null;
        extraPartses = null;
        engines = null;
        powerCores = null;
        content = null;
    }

    public void loadContent(ArrayList<VisibleObject> content) {
        this.content = content;
    }

    public void addBackgroundObjects( ArrayList<BackgroundObject> backgroundObjects ) {
        this.backgroundObjects = backgroundObjects;
    }
    public void addAsteroidTypes ( ArrayList<AsteroidType> asteroidTypes ) {
        this.asteroidTypes = asteroidTypes;
    }
    public void addLevels ( ArrayList<Level> levels ) {
        this.levels = levels;
    }
    public void addMainBodies ( ArrayList<MainBody> mainBodies ) {
        this.mainBodies = mainBodies;
    }
    public void addCannons ( ArrayList<Cannon> cannons ) {
        this.cannons = cannons;
    }
    public void addExtraParts ( ArrayList<ExtraParts> extraPartses ) {
        this.extraPartses = extraPartses;
    }
    public void addEngines ( ArrayList<Engine> engines ) {
        this.engines = engines;
    }
    public void addPowerCores ( ArrayList<PowerCore> powerCores ) {
        this.powerCores = powerCores;
    }
    public void addStarField ( StarField starField ) {
        this.starField = starField;
    }
}
