package com.example.progettolam.DB

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import androidx.room.TypeConverter

data class ActivityJoin(
    @Embedded val baseActivity: BaseActivity,

    @Relation(
        parentColumn = "id",
        entityColumn = "activityId"
    )
    val runningActivity: RunningActivity?,

    @Relation(
    parentColumn = "id",
    entityColumn = "activityId"
    )
    val drivingActivity: DrivingActivity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "activityId"
    )
    val walkingActivity: WalkingActivity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "activityId"
    )
    val sittingActivity: SittingActivity?

)
