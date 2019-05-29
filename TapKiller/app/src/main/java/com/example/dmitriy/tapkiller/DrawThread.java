package com.example.dmitriy.tapkiller;

import android.content.res.Resources;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class DrawThread extends Thread{
    private MotionEvent motionEvent;
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Bitmap background;
    private Matrix backgroundMatrix;
    private Matrix matrix;
    //private Stump stump;
    private GameObjects gameObjects;
    RabbitLifeIndicator rabbitLifeIndicator;
    StatisticIndicator statisticIndicator;
    private long prevTime;
    Random random;
    PointF screenSize;
    Canvas canvas;

    DisplayMetrics displayMetrics;

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources, GameObjects gameObjects){
        displayMetrics = resources.getDisplayMetrics();
        this.gameObjects = gameObjects;
        this.surfaceHolder = surfaceHolder;
        random = new Random(System.currentTimeMillis());
        Canvas canvas = surfaceHolder.lockCanvas(null);
        screenSize = new PointF(canvas.getWidth(), canvas.getHeight());
        surfaceHolder.unlockCanvasAndPost(canvas);
        background = BitmapFactory.decodeResource(resources, R.drawable.grass_texture);
        backgroundMatrix = new Matrix();
        backgroundMatrix.setScale(displayMetrics.widthPixels / (float)background.getWidth(),
                displayMetrics.heightPixels / (float)background.getHeight());
        backgroundMatrix.postTranslate(0,0);
        rabbitLifeIndicator = new RabbitLifeIndicator(resources,
                new Rect(0,0,displayMetrics.widthPixels, 60));
        statisticIndicator = new StatisticIndicator(gameObjects.gameStatistic, new Rect(0, 60,
                displayMetrics.widthPixels, 120));
        matrix = new Matrix();
        // формируем матрицу преобразований для картинки
        //stump = new Stump(new PointF(Math.abs(random.nextInt() % (screenSize.x-200)) + 100, Math.abs(random.nextInt() % (screenSize.y - 200)) + 100), resources);
        // сохраняем текущее время
        prevTime = System.currentTimeMillis();
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }


    private Bitmap adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas tempCanvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        tempCanvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    public void drawEnemies(){
        for (Enemy x: gameObjects.enemies) {
            Bitmap tempBitmap = x.getNextFrame();
            Matrix tempMatrix = x.getFrameMatrix();
            if (x.alive){
                canvas.drawBitmap(tempBitmap, tempMatrix, null);
            }
            else{
                int opacity =  255 * ( (int)(long) (System.currentTimeMillis() - x.timeOfDying)) / 5000;
                if (opacity > 255){
                    opacity = 255;
                }
                opacity = 255 - opacity;
                canvas.drawBitmap(adjustOpacity(tempBitmap, opacity), tempMatrix, null);
            }
        }
    }

    public void drawInfo(){

        statisticIndicator.drawIndicator(canvas);
        rabbitLifeIndicator.drawIndicator(canvas, gameObjects.rabbit.getStartLife(), gameObjects.rabbit.getLife());
    }

    public void drawGameOver(){

        int centerX = displayMetrics.widthPixels / 2;
        int centerY = displayMetrics.heightPixels / 2;
        Rect gameOverRect = new Rect(centerX - displayMetrics.widthPixels/4 , centerY - 50,
                centerX + displayMetrics.widthPixels/4 , centerY + 50);
        Paint newPaint = new Paint();
        newPaint.setColor(Color.argb(150, 150, 150, 150));
        newPaint.setStrokeWidth(5);
        newPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //canvas.drawRect(indicatorArea, newPaint);
        canvas.drawRoundRect(new RectF(gameOverRect), 2, 2, newPaint);
        newPaint.setColor(Color.argb(150, 100, 100, 100));
        newPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(gameOverRect), 5, 5, newPaint);

        newPaint.setTextAlign(Paint.Align.CENTER);
        newPaint.setColor(Color.argb(200, 0, 0, 0));
        newPaint.setTextSize(40);
        String statistic = "Кролик погиб ;(";
        canvas.drawText(statistic, gameOverRect.left + gameOverRect.width()/2,
                gameOverRect.top + gameOverRect.height()/2, newPaint);
    }

    @Override
    public void run() {
        while (runFlag) {
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;
            if (elapsedTime > 30){
                prevTime = now;
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        canvas.drawBitmap(background, backgroundMatrix,null);
                        drawEnemies();
                        canvas.drawBitmap(gameObjects.rabbit.getNextFrame(), gameObjects.rabbit.getFrameMatrix(), null);
                        drawInfo();
                        if (gameObjects.gameStatistic.gameOver){
                            drawGameOver();
                        }
                    }
                }
                catch(NullPointerException ex){
                    Log.i("", ex.toString());
                }
                finally {
                    if (canvas != null) {
                        // отрисовка выполнена. выводим результат на экран
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
