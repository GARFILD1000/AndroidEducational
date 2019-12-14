package com.example.clapsdetector.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.clapsdetector.ClapsDetectionService
import com.example.clapsdetector.model.Clap
import com.example.clapsdetector.adapter.ClapsListAdapter
import com.example.clapsdetector.R
import com.example.clapsdetector.repository.Preferences
import com.example.clapsdetector.util.ClapsDetector
import com.example.clapsdetector.viewmodel.ClapsDataViewModel
import kotlinx.android.synthetic.main.fragment_claps_data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt
import kotlin.math.sin

class ClapsDataFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    var serviceManager: WorkManager? = null
    val clapsDetector = ClapsDetector()

    var maxFrequency = 20000f
    var minFrequency = 0f
    var sensitivity = 0.95f

    val clapsAdapter = ClapsListAdapter()
    val clapsListLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    lateinit var clapsDataViewModel: ClapsDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_claps_data, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clapsList.apply {
            adapter = clapsAdapter
            layoutManager = clapsListLayoutManager
            setHasFixedSize(true)
        }
        serviceManager = WorkManager.getInstance(context!!)

        sensitivity = sensitivitySeekBar.progress / 1000f
        minFrequency = minFrequencySeekBar.progress.toFloat()
        maxFrequency = maxFrequencySeekBar.progress.toFloat()

        sensitivitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                sensitivity = progress.toFloat() / 1000f
                sensitivityTextView.setText(sensitivity.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        maxFrequencySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                maxFrequency = progress.toFloat()
                maxFrequencyTextView.setText(maxFrequency.toString())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        minFrequencySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                minFrequency = progress.toFloat()
                minFrequencyTextView.setText(minFrequency.toString())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        clapsDataViewModel = ViewModelProviders.of(activity!!).get(ClapsDataViewModel::class.java)
        clapsDataViewModel.claps.observe(viewLifecycleOwner, object : Observer<List<Clap>> {
            override fun onChanged(claps: List<Clap>?) {
                claps?.let {
                    clapsAdapter.setItems(it)
                    Toast.makeText(context, "Clap!", Toast.LENGTH_SHORT).show()
                }
            }
        })
        clapsDataViewModel.lastServiceId.observe(viewLifecycleOwner, Observer { id ->
            Preferences.saveServiceId(id)
            clapsDataViewModel.listening.postValue(id != null)
        })
        clapsDataViewModel.listening.observe(viewLifecycleOwner, Observer { listening ->
            context ?: return@Observer
            if (listening) {
                listenImageView.setImageDrawable(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_mic)
                )
            } else {
                listenImageView.setImageDrawable(
                    ContextCompat.getDrawable(context!!, R.drawable.ic_mic_off)
                )
            }
        })

        startClapButton.setOnClickListener {
            //startService()
            startThread()
            clapsDataViewModel.repo.deleteAllClaps()
        }

        stopClapButton.setOnClickListener {
            //stopService()
            stopThread()
        }
    }




    fun startThread() {
        clapsDataViewModel.listening.postValue(true)
        clapsDetector.prepare()
        clapsDetector.onSpectrumCalculated = { spectrum ->
            val frequencyStep =
                (clapsDetector.enabledSampleRate / clapsDetector.fftWindowSize)
            var frequency = 0
            var max = 0f
            for (i in spectrum.indices) {
                if (spectrum[i] > max) {
                    max = spectrum[i]
                    frequency = i
                }
            }
            currentFrequency.setText(
                getString(
                    R.string.frequency,
                    (frequency * frequencyStep).toString()
                )
            )
            spectrumView?.spectrum = spectrum
        }
        clapsDetector.onAmplitudeCalculated = { amplitude ->
            amplitudeView?.let { amplitudeSurfaceView ->
                amplitudeSurfaceView.amplitudes.addAll(amplitude)
                while (amplitudeSurfaceView.amplitudes.size > amplitudeSurfaceView.amplitudesCount) {
                    amplitudeSurfaceView.amplitudes.removeFirst()
                }
                //Log.d("ClapsDetector","Amplitudes: ${amplitudeSurfaceView.amplitudes.joinToString(" ")}")
                amplitudeSurfaceView.drawAmplitudes()
            }
        }

        clapsDetector.onParametersCalculated = { spectrum, amplitudes ->
            val frequencyStep = clapsDetector.enabledSampleRate / clapsDetector.fftWindowSize
            val lowBorder = minFrequency.toInt() //Hz
            val topBorder = maxFrequency.toInt() //Hz
            val threshold = 1f - sensitivity
            var frequency = 0
            var max = 0f
            for (i in lowBorder / frequencyStep until topBorder / frequencyStep) {
                if (i >= spectrum.size) break
                if (spectrum[i] > threshold && spectrum[i] > max) {
                    max = spectrum[i]
                    frequency = i
                }
            }
            max = 0f
            for (i in amplitudes) {
                if (i > max) max = i
            }
            if (max > threshold && frequency != 0) {
                launch (Dispatchers.Main){
                    Toast.makeText(context, "Clap! Frequency = ${frequency * frequencyStep} Hz", Toast.LENGTH_SHORT).show()
                }
            }
        }
        clapsDetector.startListening()

    }

    fun stopThread() {
        clapsDataViewModel.listening.postValue(false)
        clapsDetector.stopListening()
    }
}