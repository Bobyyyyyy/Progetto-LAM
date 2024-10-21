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
    @Query("SELECT * FROM base_activity_table WHERE startDate = :startDate")
    fun getListOfActivities(startDate: LocalDate?): LiveData<List<ActivityJoin>>

    @Transaction
    @Query("SELECT * FROM base_activity_table WHERE activityType = :activityType")
    fun getActivitiesFromType(activityType: ActivityType?): LiveData<List<ActivityJoin>>

    @Transaction
    @Query("""SELECT sum(tot_steps) FROM
             (SELECT sum(steps) as tot_steps FROM base_activity_table JOIN WalkingActivity_table ON id=activityId WHERE endDate = :endDate
                UNION
              SELECT sum(steps) as tot_steps FROM base_activity_table JOIN RunningActivity_table ON id=activityId WHERE endDate = :endDate)
            """)
    fun getAllStepsFromDay(endDate: LocalDate?) : LiveData<Int>


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
