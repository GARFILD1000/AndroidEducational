package com.example.clapsdetector.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.clapsdetector.App
import java.util.*

object Preferences{
    const val SHARED_PREF_TITLE = "com.example.clapsdetector.preferences"

    const val PREF_SERVICE_ID = "PREF_SERVICE_ID"

    private var pref: SharedPreferences = App.getContext().getSharedPreferences(SHARED_PREF_TITLE, Context.MODE_PRIVATE)


    fun saveServiceId(id: UUID?){
        val editor = pref.edit()
        editor.putString(PREF_SERVICE_ID, id?.toString())
        editor.apply()
    }

    fun loadServiceId(): UUID?{
        var serviceId: UUID? = null
        pref.getString(PREF_SERVICE_ID, null)?.let{
            serviceId = UUID.fromString(it)
        }
        return serviceId
    }
}