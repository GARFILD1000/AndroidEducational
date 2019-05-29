package com.example.dmitriy.tapkiller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class MenuBackgroundSurface extends SurfaceView implements SurfaceHolder.Callback  {
    private BackgroundThread backgroundThread;
    long prevTime;
    public MenuBackgroundSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        backgroundThread = new BackgroundThread(getHolder(), getResources());
        backgroundThread.setRunning(true);
        backgroundThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // завершаем работу потока
        backgroundThread.setRunning(false);
        while (retry) {
            try {
                backgroundThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // если не получилось, то будем пытаться еще и еще
            }
        }
    }

    public class BackgroundThread extends Thread{
        boolean runFlag;
        Bitmap background;
        Bitmap repeatBackground;
        Matrix backgroundMatrix;
        Matrix repeatBackgroundMatrix;
        Canvas canvas;
        long positionX;
        long repeatPositionX;
        Random random;
        SurfaceHolder surfaceHolder;

        DisplayMetrics displayMetrics;
        public BackgroundThread(SurfaceHolder surfaceHolder, Resources resources){
            random = new Random(System.currentTimeMillis());
            this.surfaceHolder = surfaceHolder;
            background = BitmapFactory.decodeResource(resources, R.drawable.menu_background);
            repeatBackground = BitmapFactory.decodeResource(resources, R.drawable.menu_background);
            backgroundMatrix = new Matrix();
            repeatBackgroundMatrix = new Matrix();
            prevTime = System.currentTimeMillis();
            displayMetrics = resources.getDisplayMetrics();
            positionX = 0;
            repeatPositionX = background.getWidth();
        }

        public void setRunning(boolean run) {
            runFlag = run;
        }

        @Override
        public void run() {
            while (runFlag) {
                long now = System.currentTimeMillis();
                long elapsedTime = now - prevTime;
                if (elapsedTime > 30) {
                    positionX -= 2;
                    repeatPositionX -= 2;
                    if (Math.abs(positionX) > background.getWidth()){
                        Bitmap tempBitmap = repeatBackground;
                        repeatBackground = background;
                        background = tempBitmap;
                        positionX = 0;
                        repeatPositionX = background.getWidth();
                    }
                    backgroundMatrix.setScale(displayMetrics.heightPixels / (float) background.getHeight(),
                            displayMetrics.heightPixels / (float) background.getHeight());
                    repeatBackgroundMatrix.setScale(displayMetrics.heightPixels / (float) background.getHeight(),
                            displayMetrics.heightPixels / (float) background.getHeight());
                    backgroundMatrix.postTranslate(positionX, 0);
                    repeatBackgroundMatrix.postTranslate(repeatPositionX, 0);
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        synchronized (surfaceHolder) {
                            canvas.drawBitmap(background, backgroundMatrix,null);
                            canvas.drawBitmap(repeatBackground, repeatBackgroundMatrix,null);
                        }
                    }
                    catch(NullPointerException ex){
                        Log.i("", ex.toString());
                    }
                    finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }

                }
            }
        }
    }

}
