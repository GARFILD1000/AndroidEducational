package com.example.dmitriy.tapkiller;

import android.content.res.Resources;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Random;

public class ProcessingThread extends Thread {

    private boolean runFlag = false;
    Random random;
    DisplayMetrics displayMetrics;
    SurfaceHolder surfaceHolder;
    GameObjects gameObjects;
    long prevTime;



    public ProcessingThread(SurfaceHolder surfaceHolder, Resources resources, GameObjects gameObjects) {
        random = new Random(System.currentTimeMillis());
        this.gameObjects = gameObjects;
        this.surfaceHolder = surfaceHolder;
        displayMetrics = resources.getDisplayMetrics();
        prevTime = System.currentTimeMillis();

    }

    public void setRunning(boolean run) {
        runFlag = run;
    }


    @Override
    public void run() {
        while (runFlag) {
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;
            if (elapsedTime > 30 && !gameObjects.gameStatistic.gameOver) {
                prevTime = now;
                try {
                    synchronized (surfaceHolder) {
                        gameObjects.gameStatistic.time = now - gameObjects.gameStatistic.startTime;
                        for (Enemy x : gameObjects.enemies) {
                            if (!x.isMoving()) {
                                if (random.nextInt() % (100) == 0) {
                                    x.setMovingPoint(new PointF(gameObjects.rabbit.getPosition().x + random.nextInt() % 50 - 25, gameObjects.rabbit.getPosition().y + random.nextInt() % 50 - 25));
                                    //x.setMovingPoint(new PointF(Math.abs(random.nextInt() % (screenSize.x - 200)), Math.abs(random.nextInt() % (screenSize.y - 200))));
                                }
                            } else {
                                x.move();
                            }
                            if (x.alive && x.hitBox.intersect(gameObjects.rabbit.getHitBox())) {
                                x.alive = false;
                                x.timeOfDying = System.currentTimeMillis();
                                gameObjects.rabbit.getDamage(x.life);
                                if (gameObjects.rabbit.getLife() <= 0){
                                    gameObjects.gameStatistic.gameOver = true;
                                }
                            }
                        }

                        if (!gameObjects.rabbit.isMoving()) {
                            if (random.nextInt() % (100) == 0) {
                                gameObjects.rabbit.setMovingPoint(new PointF(Math.abs(displayMetrics.widthPixels / 2 - random.nextInt() % (100) - 50),
                                        Math.abs(displayMetrics.heightPixels / 2 - random.nextInt() % (100) - 50)));
                            }
                        } else {
                            gameObjects.rabbit.move();
                        }
                        for (Enemy x : gameObjects.enemies) {
                            if (!x.alive && (System.currentTimeMillis() - x.timeOfDying > 10000)) {
                                gameObjects.enemies.remove(x);
                                break;
                            }
                        }
                    }
                }
                catch(NullPointerException ex){
                    Log.i("", ex.toString());
                }
                finally {
                }
            }

        }
    }
}