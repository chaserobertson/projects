package edu.byu.cs.superasteroids.model;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class PowerCore extends ShipPart {
    public int cannonBoost;
    public int engineBoost;
    public String image;

    public PowerCore(int cannonBoost, int engineBoost, String image, int imageId) {
        this.cannonBoost = cannonBoost;
        this.engineBoost = engineBoost;
        this.image = image;
        this.imageID = imageId;
    }
}
