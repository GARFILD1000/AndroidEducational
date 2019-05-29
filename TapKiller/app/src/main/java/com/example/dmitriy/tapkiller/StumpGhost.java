package com.example.dmitriy.tapkiller;

import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.content.res.Resources;
import android.graphics.Rect;


public class StumpGhost extends Enemy{
    public static Frames stayFrames;
    public static Frames moveFrames;
    public static Frames dieFrames;
    public static Frames hitFrames;
    StumpGhost(PointF pos,  Resources resources) {
        super(pos);
        if (stayFrames == null) {
            stayFrames = new Frames();
            moveFrames = new Frames();
            dieFrames = new Frames();
            hitFrames = new Frames();
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay1), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay2), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay3), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay4), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay5), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay6), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay7), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay8), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay9), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay10), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay11), 4);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_stay12), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_move1), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_move2), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_move3), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_move4), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_move5), 4);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_died1), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_died2), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_died3), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_died4), 5);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_died5), 0);
            hitFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.stump_ghost_hitted1), 5);
        }
        stayFramesManager = new FramesManager(stayFrames);
        moveFramesManager = new FramesManager(moveFrames);
        hitFramesManager = new FramesManager(hitFrames);
        dieFramesManager = new FramesManager(dieFrames);
        size.x = stayFramesManager.getNextFrame().getWidth();
        size.y = stayFramesManager.getNextFrame().getHeight();
        speed = 2f;
        life = 200;
        updateHitbox();
    }
}
