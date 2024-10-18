package com.example.progettolam.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import java.time.LocalDate

@Dao
interface ActivityDao {


    @Transaction
    @Query("SELECT * FROM base_activity_table")
    fun getListOfActivities(): LiveData<List<ActivityJoin>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBaseActivity(activity: BaseActivity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWalkingActivity(activity: WalkingActivity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRunningActivity(activity: RunningActivity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDrivingActivity(activity: DrivingActivity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSittingActivity(activity: SittingActivity)



}
