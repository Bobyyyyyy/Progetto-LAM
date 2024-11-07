package com.example.progettolam.DB

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime


@Entity(tableName = "base_activity_table",
    indices = [Index(value = ["id","imported", "author"], unique = true)])
class BaseActivity(

    @PrimaryKey
    @SerializedName("id")
    var id: String,

    @SerializedName("imported")
    var imported: Boolean? = false,

    @SerializedName("author")
    var author: String?,

    @SerializedName("activityType")
    var activityType: ActivityType?,

    @SerializedName("startTime")
    var startTime: LocalTime?,

    @SerializedName("startDate")
    var startDate: LocalDate?,

    @SerializedName("endTime")
    var endTime: LocalTime?,

    @SerializedName("endDate")
    var endDate: LocalDate?
)

