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
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ClapsDetector: OnsetHandler {

    companion object {
        val rates = intArrayOf(8000, 11025, 22050, 44100, 48000)
        val channels = intArrayOf(AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO)
        val encodings = intArrayOf(AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT)

        const val LOG_TAG = "ClapsDetector"
    }

    private var enabledChannels = AudioFormat.CHANNEL_IN_MONO
    private var enabledSampleRate = 8000
    private var enabledEncoding = AudioFormat.ENCODING_PCM_16BIT
    private var channelsCount = 1
    private var minBufferSize = 0
    private var recordingSampleRate = 8000
    private var bytesPerSecond = 0
    private var listeningPeriodicPause = 20L
    private var tarsosFormat: TarsosDSPAudioFormat? = null
    var sensitivity: Double = 0.0
    var threshold: Double = 0.0

    private var onDetected: ((time: Double, intensity: Double)->Unit)? = null

    var listening = false
        private set
    var listeningThread: Thread? = null

    private var recorder: AudioRecord? = null
    private var percussionOnsetDetector: PercussionOnsetDetector? = null

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

    fun start(){
        getBestAudioSettings()

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            recordingSampleRate,
            enabledChannels,
            enabledEncoding,
            minBufferSize*10
        )

        percussionOnsetDetector = PercussionOnsetDetector(
            recordingSampleRate.toFloat(),
            minBufferSize,
            this,
            sensitivity,
            threshold
        )

        tarsosFormat = TarsosDSPAudioFormat(
            recordingSampleRate.toFloat(),
            if (enabledEncoding == AudioFormat.ENCODING_PCM_16BIT) 16 else 8,
            channelsCount,
            true,
            false
        )

        startListening()
    }

    private fun startListening(){
        recorder?: return
        listening = true

//        listeningThread?.interrupt()
        listeningThread = Thread{
            Log.d(LOG_TAG,"in thread")
            val buffer = ByteArray(minBufferSize*2)
            recorder?.startRecording()
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
                if (recordingResult != AudioRecord.ERROR){
                    val audioEvent = AudioEvent(tarsosFormat)
                    audioEvent.floatBuffer = byteArrayToFloatArray(buffer)
                    percussionOnsetDetector?.process(audioEvent)
                }
                SystemClock.sleep(listeningPeriodicPause)
            }
            recorder?.stop()
        }

        listeningThread?.start()
    }

    fun stop(){
        listening = false
        listeningThread?.interrupt()
    }

    override fun handleOnset(time: Double, intensity: Double) {
        Log.d("Clap", "time = $time intensity = $intensity")
        onDetected?.invoke(time, intensity)
    }

    fun byteArrayToFloatArray(byteArray: ByteArray):FloatArray{
        val floatArray = FloatArray(byteArray.size/2)
        val byteBuffer = ByteBuffer.allocate(2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        for(i in floatArray.indices){
            byteBuffer.put(byteArray[i*2])
            byteBuffer.put(byteArray[i*2+1])
            floatArray[i] = byteBuffer.getShort(0).toFloat()
            byteBuffer.clear()
        }
        return floatArray
    }

}