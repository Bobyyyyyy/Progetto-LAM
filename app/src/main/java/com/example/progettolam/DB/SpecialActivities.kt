package com.example.progettolam.DB

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "WalkingActivity_table",
    foreignKeys = [ForeignKey(
        entity = BaseActivity::class,
        parentColumns = ["id"],
        childColumns = ["activityId"],
        onDelete = ForeignKey.CASCADE
    )])
data class WalkingActivity (
    @PrimaryKey(autoGenerate = false)
    var activityId: String,

    @SerializedName("steps")
    val steps: Int?,

    @SerializedName("avgSpeed")
    val avgSpeed: Float?
)

@Entity(tableName = "RunningActivity_table",
    foreignKeys = [ForeignKey(
        entity = BaseActivity::class,
        parentColumns = ["id"],
        childColumns = ["activityId"],
        onDelete = ForeignKey.CASCADE
    )])
data class RunningActivity (
    @PrimaryKey(autoGenerate = false)
    var activityId: String,

    @SerializedName("steps")
    val steps: Int?,

    @SerializedName("avgSpeed")
    val avgSpeed: Float?
)

@Entity(tableName = "SittingActivity_table",
    foreignKeys = [ForeignKey(
        entity = BaseActivity::class,
        parentColumns = ["id"],
        childColumns = ["activityId"],
        onDelete = ForeignKey.CASCADE
    )])
data class SittingActivity(
    @PrimaryKey(autoGenerate = false)
    var activityId: String,
)

@Entity(tableName = "DrivingActivity_table",
    foreignKeys = [ForeignKey(
        entity = BaseActivity::class,
        parentColumns = ["id"],
        childColumns = ["activityId"],
        onDelete = ForeignKey.CASCADE
    )])
data class DrivingActivity(
    @PrimaryKey(autoGenerate = false)
    var activityId: String,

    @SerializedName("avgSpeed")
    val avgSpeed: Float?
)
