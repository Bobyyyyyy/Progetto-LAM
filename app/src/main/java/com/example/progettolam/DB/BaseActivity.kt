package com.example.progettolam.DB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime


@Entity(tableName = "base_activity_table")
data class BaseActivity(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Long?,

    @SerializedName("activityType")
    var activityType: ActivityType,

    @SerializedName("startTime")
    var startTime: LocalTime?,

    @SerializedName("startDate")
    var startDate: LocalDate?,

    @SerializedName("endTime")
    var endTime: LocalTime?,

    @SerializedName("endDate")
    var endDate: LocalDate?
)

