package edu.byu.cs.superasteroids.model;

import android.graphics.PointF;

/**
 * Created by chaserobertson on 5/16/16.
 */
public class Cannon extends ShipPart {
    public PointF attachPoint;
    public PointF emitPoint;
    public String image;
    public int imageWidth;
    public int imageHeight;
    public Projectile attack;
    public String attackImage;
    public int attackImageWidth;
    public int attackImageHeight;
    public String attackSound;
    public Sound sound;
    public int damage;
    public int attackImageId;
    public int attackSoundId;

    public Cannon(String attachPoint, String emitPoint, String image, int imageWidth,
                  int imageHeight, String attackImage, int attackImageWidth, int attackImageHeight,
                  String attackSound, int damage, int imageId, int attackImageId, int attackSoundId) {

        int commaIndex = attachPoint.indexOf(",");
        String xCoordinate = attachPoint.substring(0, commaIndex);
        String yCoordinate = attachPoint.substring(commaIndex+1, attachPoint.length());

        this.attachPoint = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));

        commaIndex = emitPoint.indexOf(",");
        xCoordinate = emitPoint.substring(0, commaIndex);
        yCoordinate = emitPoint.substring(commaIndex+1, emitPoint.length());

        this.emitPoint = new PointF(Float.valueOf(xCoordinate), Float.valueOf(yCoordinate));

        this.image = image;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.attack = new Projectile(this);
        this.attackImage = attackImage;
        this.attackImageWidth = attackImageWidth;
        this.attackImageHeight = attackImageHeight;
        this.attackSound = attackSound;
        this.sound = new Sound(attackSound, attackSoundId);
        this.damage = damage;
        this.imageID = imageId;
        this.attackImageId = attackImageId;
        this.attackSoundId = attackSoundId;
    }
}
