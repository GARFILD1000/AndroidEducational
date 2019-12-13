package com.example.clapsdetector.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class SpectrumSurfaceView: SurfaceView, SurfaceHolder.Callback2 {
    constructor(context: Context): super(context)
    constructor(context: Context, attribs: AttributeSet) : super(context, attribs)
    constructor(context: Context, attribs: AttributeSet, defStyle: Int): super(context, attribs, defStyle)

    var ableToDraw = true

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        ableToDraw = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {}

    var spectrum: Array<Float>? = null
    set(value) {
        field = value
        value ?: return
        if (value.size < 2) return
        if (!ableToDraw) return

        val spectrumArraySize = value.size
        val maxAmplitudeHeight = height.toFloat()
        val maxSpectrumWidth = width.toFloat()
        val spectrumStep = maxSpectrumWidth / spectrumArraySize.toFloat()
        currentSpectrumPath = Path()
        currentSpectrumPath?.moveTo(0f, 0f)
        for (i in 0 until spectrumArraySize) {
            currentSpectrumPath?.lineTo(spectrumStep * i, value[i] * maxAmplitudeHeight)
        }
        currentSpectrumPath?.lineTo(maxSpectrumWidth, 0f)
        drawSpectrum()
    }

    var currentSpectrumPath: Path? = null

    val currentSpectrumPaint = Paint().apply{
        this.color = Color.BLUE
        this.strokeWidth = 2f
    }

    fun drawSpectrum() {
        val canvas = holder.lockCanvas()
        canvas?.drawColor(Color.BLACK)
        currentSpectrumPath?.let {path ->
            canvas?.drawPath(path, currentSpectrumPaint)
        }
        holder.unlockCanvasAndPost(canvas)
    }

    override fun surfaceRedrawNeededAsync(holder: SurfaceHolder?, drawingFinished: Runnable?) {

        super.surfaceRedrawNeededAsync(holder, drawingFinished)
    }
}