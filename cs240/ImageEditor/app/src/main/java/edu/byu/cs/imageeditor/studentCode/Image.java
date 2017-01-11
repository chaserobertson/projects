package edu.byu.cs.imageeditor.studentCode;

//import android.util.Log;

import java.lang.Math;

/**
 * Created by chaserobertson on 5/1/16.
 */
public class Image {

    public  Pixel[][] pixeltable;
    public int height = 0;
    public int width = 0;
    public int MAXVAL = 255;

    public Image(int width, int height, int MAXVAL, Pixel[][] pixeltable) {
        this.width = width;
        this.height = height;
        this.pixeltable = pixeltable;
        this.MAXVAL = MAXVAL;
    }

    public String invert() {
        Pixel[][] newPixeltable = new Pixel[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                newPixeltable[i][j] = new Pixel();
                newPixeltable[i][j].red = (MAXVAL - pixeltable[i][j].red);
                newPixeltable[i][j].green = (MAXVAL - pixeltable[i][j].green);
                newPixeltable[i][j].blue = (MAXVAL - pixeltable[i][j].blue);
                newPixeltable[i][j].MAXVAL = MAXVAL;
                //Log.d("Terd", String.valueOf(pixeltable[i][j].red));
                //Log.d("Test", String.valueOf(newPixeltable[i][j].red));
            }
        }

        return makeString(newPixeltable);
    }

    public String motionblur(int blurLength) {
        Pixel[][] newPixeltable = new Pixel[height][width];

        for(int j = width-1; j >= 0; j--) {
            for(int i = height-1; i >= 0; i--) {

                int newPixelRed = 0;
                int newPixelGreen = 0;
                int newPixelBlue = 0;
                int newPixelCount = 0;
                for(int k = 0; k < blurLength; k++) {
                    if(j + k < width) {
                        newPixelRed += pixeltable[i][j+k].red;
                        newPixelGreen += pixeltable[i][j+k].green;
                        newPixelBlue += pixeltable[i][j+k].blue;
                        newPixelCount++;
                    }
                }
                newPixeltable[i][j] = new Pixel(pixeltable[i][j].red, pixeltable[i][j].green, pixeltable[i][j].blue, 255);
                newPixeltable[i][j].red = newPixelRed / newPixelCount;
                newPixeltable[i][j].green = newPixelGreen / newPixelCount;
                newPixeltable[i][j].blue = newPixelBlue / newPixelCount;
                newPixeltable[i][j].MAXVAL = MAXVAL;
                if(newPixelCount == 0) {
                    newPixeltable[i][j].red = pixeltable[i][j].red;
                    newPixeltable[i][j].green = pixeltable[i][j].green;
                    newPixeltable[i][j].blue = pixeltable[i][j].blue;
                }
            }
        }

        return makeString(newPixeltable);
    }

    public String grayscale() {
        Pixel[][] newPixeltable = new Pixel[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                newPixeltable[i][j] = new Pixel();
                int sum = pixeltable[i][j].red + pixeltable[i][j].green + pixeltable[i][j].blue;
                newPixeltable[i][j].red = sum / 3;
                newPixeltable[i][j].green = sum / 3;
                newPixeltable[i][j].blue = sum / 3;
                newPixeltable[i][j].MAXVAL = MAXVAL;
            }
        }

        return makeString(newPixeltable);
    }

    public String emboss() {
        Pixel[][] newPixeltable = new Pixel[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                newPixeltable[i][j] = new Pixel();

                if(i == 0 || j == 0) {
                    newPixeltable[i][j].red = 128;
                    newPixeltable[i][j].green = 128;
                    newPixeltable[i][j].blue = 128;
                }
                else {
                    int redDiff = pixeltable[i][j].red - pixeltable[i-1][j-1].red;
                    int greenDiff = pixeltable[i][j].green - pixeltable[i-1][j-1].green;
                    int blueDiff = pixeltable[i][j].blue - pixeltable[i-1][j-1].blue;
                    int maxDiff = redDiff;
                    if(Math.abs(redDiff) < Math.abs(greenDiff)) {
                        if(Math.abs(greenDiff) < Math.abs(blueDiff)) maxDiff = blueDiff;
                        else maxDiff = greenDiff;
                    }
                    else {
                        if(Math.abs(redDiff) < Math.abs(blueDiff)) maxDiff = blueDiff;
                        else maxDiff = redDiff;
                    }

                    newPixeltable[i][j].red = 128 + maxDiff;
                    if(newPixeltable[i][j].red < 0) newPixeltable[i][j].red = 0;
                    if(newPixeltable[i][j].red > MAXVAL) newPixeltable[i][j].red = MAXVAL;
                    newPixeltable[i][j].green = 128 + maxDiff;
                    if(newPixeltable[i][j].green < 0) newPixeltable[i][j].green = 0;
                    if(newPixeltable[i][j].green > MAXVAL) newPixeltable[i][j].green = MAXVAL;
                    newPixeltable[i][j].blue = 128 + maxDiff;
                    if(newPixeltable[i][j].blue < 0) newPixeltable[i][j].blue = 0;
                    if(newPixeltable[i][j].blue > MAXVAL) newPixeltable[i][j].blue = MAXVAL;
                }
                newPixeltable[i][j].MAXVAL = MAXVAL;
            }
        }

        return makeString(newPixeltable);
    }

    public String makeString(Pixel[][] p) {
        StringBuilder output = new StringBuilder();
        output.append("P3\n");
        output.append(width + " " + height + "\n");
        output.append(255 + "\n");

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                output.append(p[i][j].red + " ");
                output.append(p[i][j].green + " ");
                output.append(p[i][j].blue + "\n");
            }
        }

        return output.toString();
    }
}
