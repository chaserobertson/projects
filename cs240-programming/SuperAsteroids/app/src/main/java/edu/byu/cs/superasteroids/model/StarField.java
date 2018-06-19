package edu.byu.cs.superasteroids.model;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class StarField extends VisibleObject {
    public String image;
    public int imageWidth = 2048;
    public int imageHeight = 2048;

    public StarField(String image, int imageId) {
        this.imageID = imageId;
        this.image = image;
    }
}
