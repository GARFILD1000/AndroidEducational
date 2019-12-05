package com.example.clapsdetector.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.clapsdetector.model.Clap

@Dao
interface ClapsDao{
    @Query("SELECT * FROM claps")
    fun getAllClaps(): LiveData<List<Clap>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClap(clap: Clap)

    @Query("DELETE FROM claps")
    fun deleteAll()

    @Delete
    fun delete(clap: Clap)
}