package com.example.clapsdetector.view

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.clapsdetector.App
import com.example.clapsdetector.R

class MainActivity : AppCompatActivity() {
    companion object{
        const val PERMISSIONS_REQUEST_CODE = 123
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this,
            R.id.navHostFragment
        )
        checkAllNeededPermissions()
    }

    private fun checkAllNeededPermissions(){
        val neededPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO)
        val allPermissionsGranted = App.checkPermissions(*neededPermissions)
        if (!allPermissionsGranted){
            App.requestPermissions(this, PERMISSIONS_REQUEST_CODE, *neededPermissions)
        }
    }
}
