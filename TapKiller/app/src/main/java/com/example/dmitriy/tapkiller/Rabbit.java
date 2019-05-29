package com.example.dmitriy.tapkiller;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Random;

public class Rabbit {
    private PointF position;
    private PointF size;
    private PointF direction;
    private PointF movingPoint;
    private Rect hitBox;
    private Matrix frameMatrix;
    private float speed;
    private boolean alive;
    private boolean damageGet;
    private boolean pooping;
    private long timeOfDying;
    private int startLife;
    private int life;

    private FramesManager stayFramesManager;
    private FramesManager moveFramesManager;
    private FramesManager dieFramesManager;
    private FramesManager hitFramesManager;
    private FramesManager talkFramesManager;
    private FramesManager complainFramesManager;
    private FramesManager thinkFramesManager;
    private FramesManager poopFramesManager;
    private static Frames stayFrames;
    private static Frames moveFrames;
    private static Frames dieFrames;
    private static Frames hitFrames;
    private static Frames complainFrames;
    private static Frames talkFrames;
    private static Frames thinkFrames;
    private static Frames poopFrames;

    public Rabbit(PointF pos, Resources resources){
        position = new PointF(pos.x, pos.y);
        size = new PointF(0, 0);
        direction = new PointF(0, 0);
        movingPoint = new PointF(pos.x, pos.y);
        frameMatrix = new Matrix();
        alive = true;
        pooping = false;
        damageGet = false;
        timeOfDying = 0;
        frameMatrix.postTranslate(position.x, position.y);
        if (stayFrames == null) {
            stayFrames = new Frames();
            moveFrames = new Frames();
            dieFrames = new Frames();
            hitFrames = new Frames();
            complainFrames = new Frames();
            thinkFrames = new Frames();
            poopFrames = new Frames();
            talkFrames = new Frames();
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_stay1), 10);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_stay2), 10);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_stay3), 10);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_stay4), 20);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_stay5), 20);
            stayFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_stay6), 20);

            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_move1), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_move2), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_move3), 4);
            moveFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_move4), 4);

            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_cry1), 8);
            dieFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_cry2), 8);

            hitFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_angry1), 10);
            hitFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_angry2), 10);

            complainFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_complaining1), 5);
            complainFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_complaining2), 5);
            complainFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_complaining3), 5);

            talkFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_talk1), 5);
            talkFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_talk2), 5);

            thinkFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_think1), 20);
            thinkFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_think2), 5);
            thinkFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_think3), 5);
            thinkFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_think4), 5);


            poopFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_move1), 20);
            poopFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_poop1), 30);
            poopFrames.addFrame(BitmapFactory.decodeResource(resources, R.drawable.rabbit_move1), 10);
        }
        stayFramesManager = new FramesManager(stayFrames);
        moveFramesManager = new FramesManager(moveFrames);
        hitFramesManager = new FramesManager(hitFrames);
        dieFramesManager = new FramesManager(dieFrames);
        thinkFramesManager = new FramesManager(thinkFrames);
        talkFramesManager = new FramesManager(talkFrames);
        complainFramesManager = new FramesManager(complainFrames);
        poopFramesManager = new FramesManager(poopFrames);

        size.x = stayFramesManager.getNextFrame().getWidth();
        size.y = stayFramesManager.getNextFrame().getHeight();
        speed = 3f;
        setLife(1000);
        int inset = 0;
        hitBox = new Rect((int)position.x + inset, (int)position.y + inset, (int)(position.x + size.x) - inset, (int)(position.y + size.y) - inset);
    }

    public void setLife(int life){
        this.startLife = life;
        this.life = life;
    }

    public int getLife(){
        return life;
    }

    public int getStartLife(){
        return startLife;
    }

    public void setMovingPoint(PointF dir){
        movingPoint.x = dir.x;
        movingPoint.y = dir.y;
        double distance = Math.sqrt(Math.pow(position.x - movingPoint.x, 2) + Math.pow(position.y - movingPoint.y, 2));
        double distance2 = movingPoint.y - position.y;
        double distance3 = movingPoint.x - position.x;
        direction.x = (float)(distance3 / distance);
        direction.y = (float)(distance2 / distance);
    }

    public void move(){
        if (Math.abs(movingPoint.x - position.x) < speed*2 && Math.abs(movingPoint.y - position.y) < speed*2) {
            direction.x = 0;
            direction.y = 0;
            return;
        }
        else if (alive){
            position.x += speed * direction.x;
            position.y += speed * direction.y;
            frameMatrix.setTranslate(position.x, position.y);
            if(direction.x > 0) {
                frameMatrix.setScale(-1, 1);
                frameMatrix.postTranslate(position.x + moveFramesManager.getCurrentFrame().getWidth(), position.y);
            }
            //Log.i(TAG,"Position: " + position.toString());
        }
        updateHitbox();
    }

    public void updateHitbox(){
        int inset = 3;
        hitBox = new Rect((int)position.x + inset, (int)position.y + inset, (int)(position.x + size.x) - inset, (int)(position.y + size.y) - inset);
    }

    public void getDamage(int damage){
        hitFramesManager.setFirstFrame();
        life -= damage;
        damageGet = true;
        if (life <= 0){
            alive = false;
        }

    }

    public Bitmap getNextFrame(){
        if(alive) {
            if (damageGet){
                if (!hitFramesManager.animationEnded()) {
                    return hitFramesManager.getNextFrame();
                }
                else{
                    damageGet = false;
                }
            }
            if (isMoving()) {
                return moveFramesManager.getNextFrame();
            } else {
                if (Math.abs((float)this.life / (float)this.startLife ) < 0.3){
                    return thinkFramesManager.getNextFrame();
                }
                else {
                    Random rand = new Random();
                    rand.setSeed(System.currentTimeMillis());
                    if (pooping || rand.nextInt() % 1000 == 0 ) {
                        pooping = true;
                        if (poopFramesManager.animationEnded()){
                            pooping = false;
                        }
                        return poopFramesManager.getNextFrame();
                    }
                    else{
                        return stayFramesManager.getNextFrame();
                    }
                }
            }
        }
        else{
            return dieFramesManager.getNextFrame();
        }
    }

    public Matrix getFrameMatrix(){
        return frameMatrix;
    }

    public boolean isMoving(){
        if(direction.x != 0 && direction.y != 0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isAtPoint(PointF point){
        if ((position.x < point.x && (position.x+moveFramesManager.getCurrentFrame().getWidth()) > point.x) &&
                (position.y < point.y && (position.y+moveFramesManager.getCurrentFrame().getWidth()) > point.y)){
            return true;
        }
        else{
            return false;
        }
    }

    public PointF getPosition(){
        return this.position;
    }

    public void setPosition(PointF position){
        this.position = position;
    }

    public Rect getHitBox(){
        return this.hitBox;
    }
}
