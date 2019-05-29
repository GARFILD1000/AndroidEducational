package com.example.dmitriy.tapkiller;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class RabbitLifeIndicator {
    Bitmap fullHeart;
    Bitmap emptyHeart;
    Bitmap halfHeart;
    //Bitmap indicatorBackground;
    Rect indicatorArea;
    Matrix bitmapMatrix;
    int heartsNumber = 10;
    float scale = 1f;
    float indicatorHeight;
    float indicatorWidth;
    int margin = 5;

    public RabbitLifeIndicator(Resources resources, Rect indicatorArea){
        this.indicatorArea = indicatorArea;
        fullHeart = BitmapFactory.decodeResource(resources, R.drawable.heart_full);
        halfHeart = BitmapFactory.decodeResource(resources, R.drawable.heart_half);
        emptyHeart = BitmapFactory.decodeResource(resources, R.drawable.heart_empty);
        //indicatorBackground = BitmapFactory.decodeResource(resources, R.drawable.indicator_background);
        bitmapMatrix = new Matrix();

        indicatorHeight = indicatorArea.height() - margin*2;
        indicatorWidth = (indicatorArea.width() - margin*2) / heartsNumber;
        if (indicatorHeight < indicatorWidth){
            scale = indicatorHeight / (float)fullHeart.getHeight();
        }
        else{
            scale = indicatorWidth / (float)fullHeart.getWidth();
        }

    }

    public void drawIndicator(Canvas canvas, int startLife, int life){
        int oneHeartLife = startLife / heartsNumber;
        Paint newPaint = new Paint();
        newPaint.setColor(Color.argb(150,150,150,150));
        newPaint.setStrokeWidth(margin-1);
        newPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //canvas.drawRect(indicatorArea, newPaint);
        canvas.drawRoundRect(new RectF(indicatorArea), 2,2,newPaint);
        newPaint.setColor(Color.argb(150,100,100,100));
        newPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(indicatorArea), margin-1,margin-1,newPaint);

        for (int i = 0; i < heartsNumber; i++){
            bitmapMatrix.setScale(scale, scale);
            bitmapMatrix.postTranslate(indicatorArea.left + i * indicatorWidth + margin,
                    indicatorArea.top + margin);
            if (oneHeartLife * i <= life && oneHeartLife * (i+1)  <= life){
                canvas.drawBitmap(fullHeart, bitmapMatrix, null);
            }
            else if (oneHeartLife * i < life && oneHeartLife * (i+1)  > life){
                canvas.drawBitmap(halfHeart, bitmapMatrix, null);
            }
            else{
                canvas.drawBitmap(emptyHeart, bitmapMatrix, null);
            }
        }



    }


}
