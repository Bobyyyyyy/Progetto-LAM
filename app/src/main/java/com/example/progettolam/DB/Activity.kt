package com.example.progettolam.DB

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@Entity(tableName = "Activity_table")
class Activity(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int?,

    @SerializedName("startTime")
    var startTime: LocalTime,

    @SerializedName("startDate")
    var startDate: LocalDate,


    @SerializedName("endTime")
    var endTime: LocalTime,

    @SerializedName("endDate")
    var endDate: LocalDate

    )


