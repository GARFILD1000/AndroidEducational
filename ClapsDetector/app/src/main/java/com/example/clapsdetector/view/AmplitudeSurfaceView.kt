package com.example.clapsdetector.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*

class AmplitudeSurfaceView: SurfaceView, SurfaceHolder.Callback2 {
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
    var amplitudesCount = 256
    var amplitudes = LinkedList<Float>()

    fun drawAmplitudes(){
        val canvas = holder.lockCanvas()
        canvas?.drawColor(Color.BLACK)
        val maxAmplitudeHeight = height.toFloat()
        val maxAmplitudesWidth = width.toFloat()
        val amplitudesStep = maxAmplitudesWidth / amplitudesCount
        for (i in 0 until amplitudesCount) {
            if (i >= amplitudes.size) break
            val height = maxAmplitudeHeight * amplitudes[i]
            val positionY = (maxAmplitudeHeight - height) / 2
            val positionX = maxAmplitudesWidth - i * amplitudesStep
            canvas?.drawLine(positionX, positionY, positionX, positionY + height, currentAmplitudesPaint)
        }
        holder.unlockCanvasAndPost(canvas)
    }

    val currentAmplitudesPaint = Paint().apply{
        this.color = Color.BLUE
        this.strokeWidth = 4f
    }

    override fun surfaceRedrawNeededAsync(holder: SurfaceHolder?, drawingFinished: Runnable?) {
        super.surfaceRedrawNeededAsync(holder, drawingFinished)
    }

}