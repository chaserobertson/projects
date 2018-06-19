package edu.byu.cs.superasteroids.model;

/**
 * Created by chaserobertson on 5/24/16.
 */
public class Sound extends MovingObject {
    public String sound;
    public Sound(String sound, int soundId) {
        this.sound = sound;
        this.imageID = soundId;
    }
}
