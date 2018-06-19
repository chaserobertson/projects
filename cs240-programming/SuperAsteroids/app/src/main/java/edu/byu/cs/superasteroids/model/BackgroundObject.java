package edu.byu.cs.superasteroids.model;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class BackgroundObject extends VisibleObject {
    public int ID;
    public String image;

    public BackgroundObject(int ID, String image, int imageId) {
        this.ID = ID;
        this.image = image;
        this.imageID = imageId;
    }
}
