package com.example.clapsdetector.util

import be.tarsos.dsp.util.Complex
import kotlin.math.*

object FFT {
    private const val TWO_PI = PI * 2.0

    fun decimationInTime(frame: Array<TComplex>, direct: Boolean = false): Array<TComplex> {
        if (frame.size == 1) return frame
        val frameHalfSize = frame.size / 2
        val frameFullSize = frame.size

        val frameOdd = Array<TComplex>(frameHalfSize) {TComplex(0.0, 0.0)}
        val frameEven = Array<TComplex>(frameHalfSize) {TComplex(0.0, 0.0)}
        for (i in 0 until frameHalfSize) {
            val j = i * 2
            frameOdd[i] = frame[j + 1]
            frameEven[i] = frame[j]
        }

        val spectrumOdd = decimationInTime(frameOdd, direct)
        val spectrumEven = decimationInTime(frameEven, direct)

        val arg = if (direct) -TWO_PI/frameFullSize.toDouble() else TWO_PI/frameFullSize.toDouble()
        val omegaPowBase = TComplex(cos(arg), sin(arg))
        var omega = TComplex(1.0, 0.0)
        val spectrum = Array<TComplex>(frameFullSize) {TComplex(0.0, 0.0)}
        for (i in 0 until frameHalfSize) {
            spectrum[i] = spectrumEven[i] + omega*spectrumOdd[i]
            spectrum[i + frameHalfSize] = spectrumEven[i] - omega*spectrumOdd[i]
            omega *= omegaPowBase
        }
        return spectrum
    }

    fun decimationInFrequency(frame: Array<TComplex>, direct: Boolean = false): Array<TComplex> {
        if (frame.size == 1) return frame
        val frameHalfSize = frame.size / 2
        val frameFullSize = frame.size

        val arg = if (direct) -TWO_PI/frameFullSize else TWO_PI/frameFullSize
        val omegaPowBase = TComplex(cos(arg), sin(arg))
        var omega = TComplex(1.0, 0.0)
        val spectrum = Array<TComplex>(frameFullSize) {TComplex(0.0, 0.0)}

        for (i in 0 until frameHalfSize) {
            spectrum[i] = frame[i] + frame[i + frameHalfSize]
            spectrum[i + frameHalfSize] = omega*(frame[i] - frame[i + frameHalfSize])
            omega *= omegaPowBase
        }

        var frameTop = Array<TComplex>(frameHalfSize) {TComplex(0.0, 0.0)}
        var frameBottom = Array<TComplex>(frameHalfSize) {TComplex(0.0, 0.0)}

        for (i in 0 until frameHalfSize) {
            frameTop[i] = spectrum[i]
            frameBottom[i] = spectrum[i + frameHalfSize]
        }

        frameTop = decimationInFrequency(frameTop, direct)
        frameBottom = decimationInFrequency(frameBottom, direct)

        for (i in 0 until frameHalfSize) {
            val j = i * 2
            spectrum[j] = frameTop[i]
            spectrum[j + 1] = frameBottom[i]
        }

        return spectrum
    }

}

object SpectrumAnalyser{}

fun main(args: Array<String>){
    val frequency = 44000 //Hz
    val sampleSize = 48000 //samples per second. Also this is max frequency we can determinate with FFT
    val step = 2
    val samples = Array<TComplex>(sampleSize){ TComplex(0.0, 0.0) }

    for (i in 0 until sampleSize) {
        val secondPart = i.toDouble() / sampleSize.toDouble()
        val func = sin(PI * secondPart * frequency)
        samples[i] = TComplex(func, 0.0)

    }

    val spectrum = FFT.decimationInTime(samples, false)
    val magnitudes = Array<Double>(sampleSize/2) { 0.0 }
    var max = 0.0
    var findedFrequency = 0
    for (i in 0 until sampleSize/2) {
        magnitudes[i] = sqrt(spectrum[i].real * spectrum[i].real + spectrum[i].image * spectrum[i].image)
        println("${magnitudes[i]}")
        if (magnitudes[i] > max){
            max = magnitudes[i]
            findedFrequency = i * 2
        }
    }
    println("MaxAmplitude = ${max}, findedFrequency = ${findedFrequency}")

}