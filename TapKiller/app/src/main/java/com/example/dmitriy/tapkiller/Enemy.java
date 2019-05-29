package com.example.dmitriy.tapkiller;
import android.graphics.*;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Enemy {
    protected PointF position;
    protected PointF size;
    protected PointF direction;
    protected PointF movingPoint;
    protected Rect hitBox;
    protected Matrix frameMatrix;

    protected float speed;
    protected boolean alive;
    protected boolean damageGet;
    protected long timeOfDying;
    protected int startLife;
    protected int life;

    protected FramesManager stayFramesManager;
    protected FramesManager moveFramesManager;
    protected FramesManager dieFramesManager;
    protected FramesManager hitFramesManager;

    public Enemy(PointF pos){
        position = new PointF(pos.x, pos.y);
        size = new PointF(0, 0);
        direction = new PointF(0, 0);
        movingPoint = new PointF(pos.x, pos.y);
        frameMatrix = new Matrix();
        alive = true;
        damageGet = false;
        timeOfDying = 0;
        frameMatrix.postTranslate(position.x, position.y);
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
            updateHitbox();
        }
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
                return stayFramesManager.getNextFrame();
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

    public PointF getPosition(){
        return this.position;
    }

    public void setPosition(PointF position){
        this.position = position;
    }

    public Rect getHitBox(){
        return this.hitBox;
    }

    public void setSize(PointF size) {
        this.size = size;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
