package com.example.clapsdetector.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.clapsdetector.App
import com.example.clapsdetector.model.Clap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ClapsRepository(): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private var clapsDao: ClapsDao
    private var claps: LiveData<List<Clap>>

    init{
        val database =
            ClapsDatabase.getInstance(App.getContext())
        clapsDao = database!!.clapsDao()
        claps = clapsDao.getAllClaps()
    }

    fun getAllClaps(): LiveData<List<Clap>> = claps

    fun insertClap(clap: Clap){
        launch {
            clapsDao.insertClap(clap)
        }
    }

    fun deleteClap(clap: Clap){
        launch {
            clapsDao.delete(clap)
        }
    }

    fun deleteAllClaps(){
        launch {
            clapsDao.deleteAll()
        }
    }
}