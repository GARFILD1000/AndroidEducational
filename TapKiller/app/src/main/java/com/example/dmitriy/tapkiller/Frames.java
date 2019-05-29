package com.example.dmitriy.tapkiller;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Frames{
    public ArrayList<Bitmap> framesCollection;
    public ArrayList<Integer> framesLength;
    public Frames(){
        framesCollection = new ArrayList<Bitmap>();
        framesLength = new ArrayList<Integer>();
    }
    public void addFrame(Bitmap frame, int clockForFrame){
        framesCollection.add(frame);
        framesLength.add(clockForFrame);
    }
}
