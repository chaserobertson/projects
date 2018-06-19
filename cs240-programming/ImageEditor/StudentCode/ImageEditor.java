package edu.byu.cs.imageeditor.studentCode;

//import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.Console;
import java.lang.Object;

/**
 * Created by chaserobertson on 5/1/16.
 */
public class ImageEditor implements IImageEditor {

    public String magicNumber = "";
    public String firstLine = "";
    public int height = 0;
    public int width = 0;
    public int MAXVAL = 0;
    public Image image;

    @Override
    public void load(BufferedReader bufferedReader) {
        try {
            magicNumber = bufferedReader.readLine();
            //if(bufferedReader.ready()) bufferedReader.readLine();
            while(true) {
                firstLine = bufferedReader.readLine();
                if(firstLine.substring(0,1) != "#") break;
            }
            firstLine = bufferedReader.readLine();
            width = Integer.parseInt( firstLine.substring(0,firstLine.length()/2) );
            height = Integer.parseInt( firstLine.substring(firstLine.length()/2 +1) );
            MAXVAL = Integer.parseInt(bufferedReader.readLine());

            Pixel[][] pixels = createPixelTable(width, height, bufferedReader);

            image = new Image(width, height, MAXVAL, pixels);
            //Log.d("Tesd", image.makeString(pixels));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pixel[][] createPixelTable(int width, int height, BufferedReader bufferedReader) {

        Pixel[][] pixels = new Pixel[height][width];
        try {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixels[i][j] = new Pixel();
                    //if(bufferedReader.ready()) {
                        Integer red = new Integer(bufferedReader.readLine());
                        //pixels[i][j].red = red.intValue();
                    //}
                    //if(bufferedReader.ready()) {
                        Integer green = new Integer(bufferedReader.readLine());
                        //pixels[i][j].green = green.intValue();
                    //}
                    //if(bufferedReader.ready()) {
                        Integer blue = new Integer(bufferedReader.readLine());
                        //pixels[i][j].blue = blue.intValue();
                    //}
                    //pixels[i][j].MAXVAL = MAXVAL;
                    pixels[i][j] = new Pixel(red.intValue(),green.intValue(),blue.intValue(),255);
                    //Log.d("Test", String.valueOf(pixels[i][j].blue));
                }
            }
            /*for(int i = 0; i < width; i++) {
                for(int j = 0; j < height; j++) {
                    //Log.d("Te", String.valueOf(pixels[i][j].blue));
                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pixels;
    }

    @Override
    public String invert() {
        return image.invert();
    }

    @Override
    public String motionblur(int blurLength) {
        return image.motionblur(blurLength);
    }

    @Override
    public String grayscale() {
        return image.grayscale();
    }

    @Override
    public String emboss() {
        return image.emboss();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
