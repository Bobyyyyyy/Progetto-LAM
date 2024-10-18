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
    var activityId: Long?,

    @SerializedName("steps")
    val steps: Int

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
    var activityId: Long?,

    @SerializedName("steps")
    val steps: Int

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
    var activityId: Long?,

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
    var activityId: Long?,

    )
