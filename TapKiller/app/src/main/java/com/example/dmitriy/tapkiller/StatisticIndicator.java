package com.example.dmitriy.tapkiller;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class StatisticIndicator {

    GameStatistic gameStatistic;
    Rect indicatorArea;
    int margin = 5;

    public StatisticIndicator(GameStatistic gameStatistic, Rect indicatorArea){
        this.gameStatistic = gameStatistic;
        this.indicatorArea = indicatorArea;
    }
    public void drawIndicator(Canvas canvas) {

        Paint newPaint = new Paint();
        newPaint.setColor(Color.argb(150, 150, 150, 150));
        newPaint.setStrokeWidth(margin - 1);
        newPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //canvas.drawRect(indicatorArea, newPaint);
        canvas.drawRoundRect(new RectF(indicatorArea), 2, 2, newPaint);
        newPaint.setColor(Color.argb(150, 100, 100, 100));
        newPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(indicatorArea), margin - 1, margin - 1, newPaint);

        newPaint.setTextAlign(Paint.Align.CENTER);
        newPaint.setColor(Color.argb(200, 0, 0, 0));
        newPaint.setTextSize(indicatorArea.height() - margin*2);
        String statistic = "УБИТО " + String.valueOf(gameStatistic.kills);
        statistic += "        ВРЕМЯ " + String.valueOf(gameStatistic.time/1000);
        canvas.drawText(statistic, indicatorArea.left +  indicatorArea.width() /2, indicatorArea.bottom - margin, newPaint);


    }
}
