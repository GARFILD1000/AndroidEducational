package com.example.dmitriy.tapkiller;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;

public class StumpFunny extends Enemy{
    public static Frames stayFrames;
    public static Frames moveFrames;
    public static Frames dieFrames;
    public static Frames hitFrames;
    public StumpFunny(PointF pos, Resources resources) {
        super(pos);
        if (stayFrames == null) {
            stayFrames = new Frames();
            moveFrames = new Frames();
            dieFrames = new Frames();
            hitFrames = new Frames();
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay1), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay2), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay3), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay4), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay5), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay6), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_stay7), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_move1), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_move2), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_move3), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_move4), 4);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_died1), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_died2), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_died3), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_died4), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_died5), 0);
            hitFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_funny_hitted1), 5);
        }
        stayFramesManager = new FramesManager(stayFrames);
        moveFramesManager = new FramesManager(moveFrames);
        hitFramesManager = new FramesManager(hitFrames);
        dieFramesManager = new FramesManager(dieFrames);
        size.x = stayFramesManager.getNextFrame().getWidth();
        size.y = stayFramesManager.getNextFrame().getHeight();
        speed = 2f;
        life = 150;
        updateHitbox();
    }
}
