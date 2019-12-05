package com.example.clapsdetector

import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class App: Application(){
    companion object{
        lateinit var instance: App

        fun getContext() = instance.applicationContext

        fun checkPermission(permission: String): Boolean{
            val currentAPIVersion = Build.VERSION.SDK_INT
            return if (currentAPIVersion >= Build.VERSION_CODES.M) {
                ContextCompat.checkSelfPermission(instance.applicationContext, permission) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }

        fun checkPermissions(vararg permissions: String): Boolean{
            val currentAPIVersion = Build.VERSION.SDK_INT
            var result = true
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                for (permission in permissions) {
                    val permissionGranted =
                        (ContextCompat.checkSelfPermission(instance.applicationContext, permission) == PackageManager.PERMISSION_GRANTED)
                    result = result && permissionGranted
                }
            }
            return  result
        }

        fun requestPermissions(activity: Activity, requestCode: Int, vararg permissions: String){
            ActivityCompat.requestPermissions(activity,  permissions, requestCode)
        }
    }



    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}