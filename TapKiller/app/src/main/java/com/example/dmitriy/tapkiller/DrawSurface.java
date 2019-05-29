package com.example.dmitriy.tapkiller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback  {
    private DrawThread drawThread;
    private ProcessingThread processingThread;
    public GameObjects gameObjects;

    private Random random;
    private long lastClick;
    private SoundPool sounds;
    private int soundBeat;
    private int soundHit1, soundHit2;
    DisplayMetrics displayMetrics;
    Timer spawnTimer;
    SpawnTask spawnTask;
    public DrawSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        displayMetrics = getResources().getDisplayMetrics();
        random = new Random(System.currentTimeMillis());
        gameObjects = new GameObjects();
        gameObjects.enemies = new LinkedList<>();
        gameObjects.rabbit = new Rabbit(new PointF(displayMetrics.widthPixels/2, displayMetrics.heightPixels/2), getResources());
        gameObjects.gameStatistic = new GameStatistic();

        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        soundBeat = sounds.load(context, R.raw.beat, 1);
        soundHit1 = sounds.load(context, R.raw.hit1, 1);
        soundHit2 = sounds.load(context, R.raw.hit2, 1);
        spawnTimer = new Timer();
        spawnTask = new SpawnTask();
        spawnTimer.schedule(spawnTask, 1000);
    }

    //@Override
    //public
    public PointF getRandomSpawnPosition(){
        PointF randomPosition = new PointF();
        randomPosition.x = random.nextInt() % (displayMetrics.widthPixels + 400) - 200;
        if ((randomPosition.x > -100 && randomPosition.x < displayMetrics.widthPixels + 100)){
            randomPosition.y = (random.nextInt() % 2 == 0) ? -200 : displayMetrics.heightPixels + 200;
        }
        else{
            randomPosition.y = random.nextInt() % (displayMetrics.heightPixels);
        }
        return randomPosition;
    }

    void spawnEnemies(){
            int newEnemiesNumber = random.nextInt() % 5 + 1;
            for (int i = 0; i < newEnemiesNumber; i++) {
                switch (random.nextInt() % 5) {
                    case 0:
                        gameObjects.enemies.add(new Stump(getRandomSpawnPosition(), getResources()));
                        break;
                    case 1:
                        gameObjects.enemies.add(new StumpAxe(getRandomSpawnPosition(), getResources()));
                        break;
                    case 2:
                        gameObjects.enemies.add(new StumpGhost(getRandomSpawnPosition(), getResources()));
                        break;
                    case 3:
                        gameObjects.enemies.add(new StumpPsycho(getRandomSpawnPosition(), getResources()));
                        break;
                    case 4:
                        gameObjects.enemies.add(new StumpFunny(getRandomSpawnPosition(), getResources()));
                        break;
                }
            }
    }

    class SpawnTask extends TimerTask{
        @Override
        public void run(){
            //Canvas tempCanvas = getHolder().lockCanvas();
            synchronized (getHolder()) {
                spawnEnemies();
                spawnTimer = new Timer();
                spawnTask = new SpawnTask();
                spawnTimer.schedule(spawnTask, 2000);
            }
            //getHolder().unlockCanvasAndPost(tempCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 200) {
            lastClick = System.currentTimeMillis();
            int counter = 0;
            synchronized (getHolder()) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !gameObjects.gameStatistic.gameOver) {
                    for (Enemy x : gameObjects.enemies) {
                        if (x.alive && x.isAtPoint(new PointF(event.getX(), event.getY()))) {
                            x.getDamage(50);
                            if (x.alive) {
                                sounds.play(((random.nextInt() % 2 == 0)? soundHit1 : soundHit2), 1.0f, 1.0f, 0, 0, 1.5f);
                            }
                            else {
                                gameObjects.gameStatistic.kills++;
                                sounds.play(soundBeat, 1.0f, 1.0f, 0, 0, 1.5f);
                                x.timeOfDying = System.currentTimeMillis();
                            }
                            //enemies.remove(x);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder(), getResources(), gameObjects);
        drawThread.setRunning(true);
        drawThread.start();

        processingThread = new ProcessingThread(getHolder(), getResources(), gameObjects);
        processingThread.setRunning(true);
        processingThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // завершаем работу потока
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
        retry = true;
        processingThread.setRunning(false);
        while (retry) {
            try {
                processingThread.join();
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
    }




}