package com.example.progettolam.DB

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import com.google.gson.annotations.SerializedName


@Entity(tableName = "Activity_table")
class Activity(

    @PrimaryKey
    @SerializedName("id")
    var id: Int,

    @SerializedName("startTime")
    var startTime: String,

    @SerializedName("endTime")
    var endTime: String,

)


