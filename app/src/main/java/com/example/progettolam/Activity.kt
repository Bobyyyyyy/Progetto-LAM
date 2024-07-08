package com.example.progettolam

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import com.google.gson.annotations.SerializedName


@Entity(tableName = "Activity_table")
class Activity(

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    var id: String,

    @SerializedName("startTime")
    var startTime: Date,

    @SerializedName("endTime")
    var endTime: Date,

)


