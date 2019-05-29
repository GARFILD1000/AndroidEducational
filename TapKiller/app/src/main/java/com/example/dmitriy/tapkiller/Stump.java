package com.example.dmitriy.tapkiller;

import android.content.res.Resources;
import android.graphics.*;

public class Stump extends Enemy {
    public static Frames stayFrames;
    public static Frames moveFrames;
    public static Frames dieFrames;
    public static Frames hitFrames;
    public Stump(PointF pos, Resources resources){
        super(pos);
        if (stayFrames == null) {
            stayFrames = new Frames();
            moveFrames = new Frames();
            dieFrames = new Frames();
            hitFrames = new Frames();
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_stay), 5);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump1), 5);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump2), 5);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump3), 5);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump4), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_died1), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_died2), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_died3), 0);
            hitFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_hitted1), 5);
        }
        stayFramesManager = new FramesManager(stayFrames);
        moveFramesManager = new FramesManager(moveFrames);
        hitFramesManager = new FramesManager(hitFrames);
        dieFramesManager = new FramesManager(dieFrames);
        size.x = stayFramesManager.getNextFrame().getWidth();
        size.y = stayFramesManager.getNextFrame().getHeight();
        speed = 4f;
        life = 50;
        updateHitbox();
    }



}
