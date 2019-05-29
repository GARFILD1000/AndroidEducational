package com.example.dmitriy.tapkiller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.*;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    DrawSurface drawSurface;
    TextView lifeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        drawSurface = new DrawSurface(this);
        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        lifeView = findViewById(R.id.lifeView);
        LinearLayout gameLayout = findViewById(R.id.gameView);
        gameLayout.addView(drawSurface);
    }




}
    /*public class DrawPanel extends SurfaceView {
        Path path;
        Thread thread = null;
        SurfaceHolder surfaceHolder;
        volatile boolean running = false;
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random random;

        public DrawPanel(Context context){
            super(context);
            surfaceHolder = getHolder();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setColor(Color.WHITE);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                path = new Path();
                path.moveTo(event.getX(), event.getY());
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE){
                path.lineTo(event.getX(), event.getY());
            }
            else if (event.getAction() == MotionEvent.ACTION_UP){
                path.lineTo(event.getX(), event.getY());
            }

            if (path != null ){
                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawPath(path, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
            return true;

        }
    }

}
*/
/*public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Panel(this));
    }
    class Panel extends View{
        private float angle = 180;
        public Panel(Context context){
            super(context);
        }
        void Drawing(Canvas canvas){
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.stumps);
            Matrix matrix = new Matrix();
            //matrix.postScale(3.0f, 3.0f);
            matrix.mapRect(new RectF(10,10,40,40));
            matrix.preRotate(angle,b.getWidth()/2, b.getHeight()/2);
            canvas.drawBitmap(b, matrix, null);

            Paint p1 = new Paint();
            p1.setColor(Color.RED);
            p1.setStyle(Paint.Style.STROKE);
            //p1.setPathEffects(new DashPathEffect(new float[]{1,20,15,20,10,30,50,10},0));
            p1.setStrokeWidth(4.5f);
            canvas.drawCircle(200,200,100, p1);
            canvas.drawLine(100,150, 900,150, p1);
        }
        @Override
        public void onDraw(Canvas canvas){
            Paint p1 = new Paint();
            p1.setColor(Color.rgb(100,255,10));
            p1.setTextSize(45);
            canvas.drawText("Resolution: "+ canvas.getHeight() + " * " + canvas.getWidth(), 10,100, p1);
            Drawing(canvas);
            angle = (angle > 360)? 0 : angle + 6f;
            invalidate();
        }
    }
}*/
