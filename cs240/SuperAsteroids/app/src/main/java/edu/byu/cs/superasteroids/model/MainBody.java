package edu.byu.cs.superasteroids.model;

import android.graphics.PointF;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class MainBody extends ShipPart {
    public PointF cannonAttach;
    public PointF engineAttach;
    public PointF extraAttach;
    public String image;
    public int imageWidth;
    public int imageHeight;

    public MainBody(String cannonAttach, String engineAttach, String extraAttach,
                    String image, int imageWidth, int imageHeight, int imageId) {
        int commaIndex = cannonAttach.indexOf(",");
        String xCoordinate = cannonAttach.substring(0, commaIndex);
        String yCoordinate = cannonAttach.substring(commaIndex+1, cannonAttach.length());
        this.cannonAttach = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));

        commaIndex = engineAttach.indexOf(",");
        xCoordinate = engineAttach.substring(0, commaIndex);
        yCoordinate = engineAttach.substring(commaIndex+1, engineAttach.length());
        this.engineAttach = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));

        commaIndex = extraAttach.indexOf(",");
        xCoordinate = extraAttach.substring(0, commaIndex);
        yCoordinate = extraAttach.substring(commaIndex+1, extraAttach.length());
        this.extraAttach = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));

        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageID = imageId;
    }
}
