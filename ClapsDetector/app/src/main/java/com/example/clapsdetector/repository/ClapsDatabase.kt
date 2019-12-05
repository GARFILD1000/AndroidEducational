package com.example.clapsdetector.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.clapsdetector.model.Clap

@Database(entities = [Clap::class], version = 1)
abstract class ClapsDatabase: RoomDatabase(){
    abstract fun clapsDao(): ClapsDao
    companion object{
        private var instance: ClapsDatabase? = null
        fun getInstance(context: Context): ClapsDatabase?{
            if (instance == null){
                synchronized(ClapsDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ClapsDatabase::class.java,
                        "claps_database.db")
                        .fallbackToDestructiveMigration()
                        .build()

                }
            }
            return instance
        }

        fun freeInstance(){
            instance = null
        }
    }
}