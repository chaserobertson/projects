package edu.byu.cs.superasteroids.model;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class AsteroidType extends VisibleObject {
    public String name;
    public String image;
    public int imageWidth;
    public int imageHeight;

    public AsteroidType(String name, String image, int imageWidth, int imageHeight, int imageId) {
        this.name = name;
        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageID = imageId;
    }
}
