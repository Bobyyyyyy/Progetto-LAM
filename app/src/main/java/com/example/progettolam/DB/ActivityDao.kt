package com.example.progettolam.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.progettolam.UI.profileFragment.ActivitiesGraphData
import com.example.progettolam.UI.profileFragment.StepsData
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
    fun getAllStepsFromDay(endDate: LocalDate?) : LiveData<Int?>


    @Transaction
    @Query("""
SELECT 
    (strftime('%w', steps_per_day.endDate)) AS day_of_week, 
    SUM(steps_per_day.tot_steps) AS daily_steps
FROM (
    SELECT endDate, SUM(steps) AS tot_steps
    FROM base_activity_table
    JOIN WalkingActivity_table ON id = activityId
    WHERE endDate >= DATE(:today, 'weekday 0') AND endDate <= DATE(:today, '+6 days')
    GROUP BY endDate
    UNION ALL
    SELECT endDate, SUM(steps) AS tot_steps
    FROM base_activity_table
    JOIN RunningActivity_table ON id = activityId
    WHERE endDate >= DATE(:today, 'weekday 0') AND endDate <= DATE(:today, '+6 days')
    GROUP BY endDate
) AS steps_per_day
GROUP BY steps_per_day.endDate
ORDER BY steps_per_day.endDate ASC
""")
    fun getCurrentWeekSteps(today: LocalDate?) : LiveData<Array<StepsData>>



@Transaction
@Query("""
    SELECT activityType as type, COUNT(*) AS number
    FROM base_activity_table
     WHERE endDate >= DATE(:today, 'weekday 0') AND endDate <= DATE(:today, '+6 days')
    GROUP BY activityType;
""")
    fun getCurrentWeekActivities(today: LocalDate?): LiveData<Array<ActivitiesGraphData>>

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

    @Query("SELECT * FROM base_activity_table")
    fun getAllBaseActivities(): List<BaseActivity>

    @Query("SELECT * FROM walkingactivity_table")
    fun getAllWalkingActivities(): List<WalkingActivity>

    @Query("SELECT * FROM runningactivity_table")
    fun getAllRunningActivities(): List<RunningActivity>

    @Query("SELECT * FROM sittingactivity_table")
    fun getAllStillActivities(): List<SittingActivity>

    @Query("SELECT * FROM drivingactivity_table")
    fun getAllDrivingActivities(): List<DrivingActivity>

}
