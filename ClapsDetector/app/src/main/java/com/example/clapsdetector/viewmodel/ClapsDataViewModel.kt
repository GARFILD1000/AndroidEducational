package com.example.clapsdetector.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clapsdetector.model.Clap
import com.example.clapsdetector.repository.ClapsRepository
import com.example.clapsdetector.repository.Preferences
import java.util.*

class ClapsDataViewModel: ViewModel(){
    val repo = ClapsRepository()
    var claps: LiveData<List<Clap>> =  repo.getAllClaps()
    val lastServiceId: MutableLiveData<UUID?> = MutableLiveData<UUID?>().apply { this.postValue(Preferences.loadServiceId()) }
}