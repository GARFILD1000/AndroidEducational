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
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.sin

class ClapsDataFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    var serviceManager: WorkManager? = null
    var service: OneTimeWorkRequest? = null

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

        sensitivitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                //clap
                val sensitivity = progress.toDouble() / 1000.0
                sensitivityTextView.setText(sensitivity.toString())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        thresholdSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                val threshold = progress.toDouble() / 1000.0
                thresholdTextView.setText(threshold.toString())
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
        clapsDataViewModel.lastServiceId.observe(viewLifecycleOwner, object: Observer<UUID?>{
            override fun onChanged(id: UUID?) {
                Preferences.saveServiceId(id)
                context?:return
                if (id == null){
                    listenImageView.setImageDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.ic_mic_off)
                    )
                } else {
                    listenImageView.setImageDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.ic_mic)
                    )
                }
            }
        })

        startClapButton.setOnClickListener {
            startService()
            clapsDataViewModel.repo.deleteAllClaps()
        }

        stopClapButton.setOnClickListener {
            stopService()
        }
    }


    fun startService(){
        clapsDataViewModel.lastServiceId.value?.let {
            serviceManager?.cancelWorkById(it)
        }
        val sensitivity = sensitivitySeekBar.progress.toDouble() / 1000.0
        val threshold = thresholdSeekBar.progress.toDouble() / 1000.0
        val dataBuilder = Data.Builder().apply {
            putDouble(ClapsDetectionService.SENSIVITY_PARAM,   sensitivity)
            putDouble(ClapsDetectionService.THRESHOLD_PARAM,    threshold)
        }
        val constraints = Constraints.Builder().build()
        val service = OneTimeWorkRequest.Builder(ClapsDetectionService::class.java)
            .setInputData(dataBuilder.build())
            .setConstraints(constraints)
            .build()
        clapsDataViewModel.lastServiceId.postValue(service.id)
        serviceManager?.enqueue(service)
    }

    fun stopService(){
        clapsDataViewModel.lastServiceId.value?.let {
            serviceManager?.cancelWorkById(it)
        }
        clapsDataViewModel.lastServiceId.postValue(null)
    }
}