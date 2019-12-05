package com.example.clapsdetector.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormatterBuilder

@Entity(tableName = "claps")
class Clap(){
    companion object{
        fun prepareTime(time: Instant): String{
            val formatter = DateTimeFormatterBuilder()
                .appendDayOfMonth(2)
                .appendLiteral(".")
                .appendMonthOfYear(2)
                .appendLiteral(".")
                .appendYear(4,4)
                .appendLiteral(" ")
                .appendHourOfDay(2)
                .appendLiteral(":")
                .appendMinuteOfHour(2)
                .appendLiteral(":")
                .appendSecondOfMinute(2)
                .appendLiteral(" UTC")
                .toFormatter()
            return formatter.print(time)
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id = 0
    var time: String = prepareTime(Instant.now())//DateTime().toString()
    var intensity: Float = 0f

    constructor(newClapIntensity: Float): this(){
        intensity = newClapIntensity
    }
}