package com.example.clapsdetector.util

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.SystemClock
import android.util.Log
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.onsets.OnsetHandler
import be.tarsos.dsp.onsets.PercussionOnsetDetector
import java.lang.Math.pow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class ClapsDetector: OnsetHandler {

    companion object {
        val rates = intArrayOf(8000, 11025, 22050/*, 44100, 48000*/)
        val channels = intArrayOf(AudioFormat.CHANNEL_IN_MONO /*, AudioFormat.CHANNEL_IN_STEREO*/)
        val encodings = intArrayOf(AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT)

        const val LOG_TAG = "ClapsDetector"
    }

    private var enabledChannels = AudioFormat.CHANNEL_IN_MONO
    var enabledSampleRate = 8000
    private set
    private var enabledEncoding = AudioFormat.ENCODING_PCM_16BIT
    set(value){
        field = value
        encodingByteCount = if (value == AudioFormat.ENCODING_PCM_16BIT) 2 else 1
    }
    var encodingByteCount = 1
    private var channelsCount = 1
    private var minBufferSize = 0
    var fftWindowSize = 1024
    private set
    val samplesPerAmplitude = 20
    private var bytesPerSecond = 0
    private var listeningPeriodicPause = 20L
    var sensitivity: Double = 0.0
    var threshold: Double = 0.0

    var onDetected: ((time: Double, intensity: Double) -> Unit)? = null
    var onSpectrumCalculated: ((spectrum: Array<Float>) -> Unit)? = null
    var onAmplitudeCalculated: ((amplitudes: List<Float>) -> Unit)? = null
    var onParametersCalculated: ((spectrum: Array<Float>, amplitudes: List<Float>) -> Unit)? = null

    var listening = false
        private set
    var listeningThread: Thread? = null

    private var recorder: AudioRecord? = null

    fun getBestAudioSettings(){
        for (enc in encodings) {
            for (ch in channels) {
                for (rate in rates) {
                    val t = AudioRecord.getMinBufferSize(rate, ch, enc)
                    if (t != AudioRecord.ERROR && t != AudioRecord.ERROR_BAD_VALUE) {
                        enabledEncoding = enc
                        enabledChannels = ch
                        enabledSampleRate = rate
                        minBufferSize = t
                    }
                }
            }
        }
        channelsCount =  if (enabledChannels == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        Log.d(LOG_TAG,"Best settings: " +
                "sample rate = ${enabledSampleRate}, " +
                "encoding = ${if (enabledEncoding == AudioFormat.ENCODING_PCM_8BIT) "8bit" else "16bit"}, " +
                "channels = ${if (enabledChannels == AudioFormat.CHANNEL_IN_MONO) "mono" else "stereo"}, " +
                "min buffer size = $minBufferSize")
        bytesPerSecond = enabledSampleRate*channelsCount
        bytesPerSecond *= if (enabledEncoding == AudioFormat.ENCODING_PCM_8BIT) 1 else 2
    }

    fun setOnDetectedListener(onDetectedListener: (time: Double, intensity: Double)->Unit){
        onDetected = onDetectedListener
    }

    fun prepare(){
        getBestAudioSettings()

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            enabledSampleRate,
            enabledChannels,
            enabledEncoding,
            minBufferSize*10
        )
    }

    fun startListening(){
        recorder?: return
        if (listening) return

        listening = true

//        listeningThread?.interrupt()
        listeningThread = Thread{
            Log.d(LOG_TAG,"in thread")
            val buffer = ByteArray(fftWindowSize * encodingByteCount)
            recorder?.startRecording()
            val complexSamples = Array<TComplex>(fftWindowSize) { TComplex(0.0,0.0)}
            val magnitudes = Array<Float>(complexSamples.size / 2) {0f}
            while(listening){
//                Log.d(LOG_TAG,"thread")
                val recordingResult = recorder?.read(buffer, 0, buffer.size)

                when(recordingResult){
                    AudioRecord.ERROR_INVALID_OPERATION -> {
                        Log.d(LOG_TAG,"ERROR_INVALID_OPERATION")
                    }
                    AudioRecord.ERROR_BAD_VALUE -> {
                        Log.d(LOG_TAG,"ERROR_BAD_VALUE")
                    }
                    AudioRecord.ERROR_DEAD_OBJECT -> {
                        Log.d(LOG_TAG,"ERROR_DEAD_OBJECT")
                    }
                    AudioRecord.ERROR -> {
                        Log.d(LOG_TAG,"ERROR")
                    }
                    AudioRecord.SUCCESS -> {
                        Log.d(LOG_TAG,"SUCCESS")
                    }
                }
//                Log.d("RecorderSize", "$recordingResult $minBufferSize")

                if (recordingResult != AudioRecord.ERROR){

                    val floatSamples = buffer.toFloatArray()
                    complexSamples.forEachIndexed { idx, value ->
                        value.real = floatSamples.getOrNull(idx)?.toDouble() ?: 0.0
                    }
                    val spectrum = FFT.decimationInTime(complexSamples)
                    for (i in 0 until magnitudes.size) {
                        magnitudes[i] = sqrt(spectrum[i].real * spectrum[i].real + spectrum[i].image * spectrum[i].image).toFloat()
                    }
                    normalize(magnitudes)

                    val amplitudes = LinkedList<Float>()
                    for (i in 0 until floatSamples.size / samplesPerAmplitude) {
                        var amplitude = 0f
                        for (j in 0 until samplesPerAmplitude) {
                            amplitude += floatSamples[i * samplesPerAmplitude + j]
                        }
                        val normalizedAmplitude = (amplitude / samplesPerAmplitude) / pow(2.0, encodingByteCount * 8.0 - 3).toFloat()
                        amplitudes.addLast(normalizedAmplitude)
                    }
                    onParametersCalculated?.invoke(magnitudes, amplitudes)
                    onSpectrumCalculated?.invoke(magnitudes)
                    onAmplitudeCalculated?.invoke(amplitudes)
                }
//                SystemClock.sleep(listeningPeriodicPause)
            }
            recorder?.stop()
        }

        listeningThread?.start()
    }

    fun stopListening(){
        listening = false
        listeningThread?.interrupt()
    }

    override fun handleOnset(time: Double, intensity: Double) {
        Log.d("Clap", "time = $time intensity = $intensity")
        onDetected?.invoke(time, intensity)
    }

    fun ByteArray.toFloatArray():FloatArray{
        val floatArray = FloatArray(this.size/2)
        val byteBuffer = ByteBuffer.allocate(2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        for(i in floatArray.indices){
            byteBuffer.put(this[i*2])
            byteBuffer.put(this[i*2+1])
            floatArray[i] = byteBuffer.getShort(0).toFloat()
            byteBuffer.clear()
        }
        return floatArray
    }

    fun normalize(array: Array<Float>){
        var max = Float.MIN_VALUE
        array.forEach { if (it > max) max = it }
        for(i in 0 until array.size) array[i] /= max
    }


}