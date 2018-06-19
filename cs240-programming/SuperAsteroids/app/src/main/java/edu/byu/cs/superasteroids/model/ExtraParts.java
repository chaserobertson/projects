package edu.byu.cs.superasteroids.model;

import android.graphics.PointF;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class ExtraParts extends ShipPart{
    public PointF attachPoint;
    public String image;
    public int imageWidth;
    public int imageHeight;

    public ExtraParts(String attachPoint, String image, int imageWidth, int imageHeight, int imageId) {
        int commaIndex = attachPoint.indexOf(",");
        String xCoordinate = attachPoint.substring(0, commaIndex);
        String yCoordinate = attachPoint.substring(commaIndex+1, attachPoint.length());
        this.attachPoint = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));

        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageID = imageId;
    }
}
