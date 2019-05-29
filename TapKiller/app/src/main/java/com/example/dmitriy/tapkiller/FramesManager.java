package com.example.dmitriy.tapkiller;

import android.graphics.Bitmap;

public class FramesManager{
    private int clockCounter;
    private boolean end;
    private int frameNumber;
    Frames frames;
    public FramesManager(Frames newFrames){
        frames = newFrames;
        frameNumber = 0;
        clockCounter = 0;
        end = false;
    }
    public Bitmap getNextFrame(){
        end = false;
        if (frames.framesLength.get(frameNumber) > 0 && clockCounter >= frames.framesLength.get(frameNumber)){
            frameNumber++;
            clockCounter = 0;
        }
        if (frameNumber >= frames.framesCollection.size()){
            frameNumber = 0;
            end = true;
        }
        clockCounter++;
        return frames.framesCollection.get(frameNumber);
    }
    public Bitmap getCurrentFrame(){
        return frames.framesCollection.get(frameNumber);
    }
    public boolean animationEnded(){
        return end;
    }
    public void setFirstFrame(){
        clockCounter = 0;
        frameNumber = 0;
        end = false;
    }
}
