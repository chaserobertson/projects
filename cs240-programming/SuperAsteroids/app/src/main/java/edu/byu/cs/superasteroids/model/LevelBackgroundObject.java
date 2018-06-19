package edu.byu.cs.superasteroids.model;

import android.graphics.PointF;


/**
 * Created by chaserobertson on 5/16/16.
 */
public class LevelBackgroundObject extends VisibleObject {
    public PointF position;
    public BackgroundObject backgroundObject;
    public float scale;

    public LevelBackgroundObject(String position, int objectId, float scale) {
        int commaIndex = position.indexOf(",");
        String xCoordinate = position.substring(0, commaIndex);
        String yCoordinate = position.substring(commaIndex+1, position.length());

        int i = 1;
        for(BackgroundObject BO : Model.SINGLETON.backgroundObjects) {
            if(objectId == i) backgroundObject = BO;
            i++;
        }

        this.position = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));
        this.scale = scale;
    }
}
