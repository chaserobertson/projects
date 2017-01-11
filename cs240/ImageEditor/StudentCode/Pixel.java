package edu.byu.cs.imageeditor.studentCode;

/**
 * Created by chaserobertson on 5/1/16.
 */
public class Pixel {
    public int red = 0;
    public int green = 0;
    public int blue = 0;
    public int MAXVAL = 255;

    public Pixel(int red, int green, int blue, int MAXVAL) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.MAXVAL = MAXVAL;
    }

    public Pixel() {

    }

    public int getRed() {
        return red;
    }
    public void setRed(int red) {
        this.red = red;
    }

    public void invert() {
        /*red = MAXVAL - red;
        if(red > MAXVAL) { red = MAXVAL; }
        if(red < 0) { red = 0; }
        green = MAXVAL - green;
        if(green > MAXVAL) { green = MAXVAL; }
        if(green < 0) { green = 0; }
        blue = MAXVAL - blue;
        if(blue > MAXVAL) { blue = MAXVAL; }
        if(blue < 0) { blue = 0; }*/
    }
    public void grayscale() {}

}
