package edu.byu.cs.imageeditor.studentCode;

import java.io.BufferedReader;

public interface IImageEditor {

    void load(BufferedReader bufferedReader);
    String invert();
    String grayscale();
    String emboss();
    String motionblur(int blurLength);
}
