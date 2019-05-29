package com.example.dmitriy.tapkiller;


import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;

public class StumpAxe extends Enemy {
    public static Frames stayFrames;
    public static Frames moveFrames;
    public static Frames dieFrames;
    public static Frames hitFrames;
    public StumpAxe(PointF pos, Resources resources){
        super(pos);
        if (stayFrames == null) {
            stayFrames = new Frames();
            moveFrames = new Frames();
            dieFrames = new Frames();
            hitFrames = new Frames();
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_stay), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_move1), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_move2), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_move3), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_move4), 4);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_died1), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_died2), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_died3), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_died4), 0);
            hitFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_axe_hitted1), 5);
        }
        stayFramesManager = new FramesManager(stayFrames);
        moveFramesManager = new FramesManager(moveFrames);
        hitFramesManager = new FramesManager(hitFrames);
        dieFramesManager = new FramesManager(dieFrames);
        size.x = stayFramesManager.getNextFrame().getWidth();
        size.y = stayFramesManager.getNextFrame().getHeight();
        setSpeed(3f);
        setLife(100);
        updateHitbox();
    }
}
