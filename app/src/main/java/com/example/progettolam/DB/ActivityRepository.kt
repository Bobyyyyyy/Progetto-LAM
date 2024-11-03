package com.example.progettolam.DB

import android.app.Application
import androidx.lifecycle.LiveData
import java.time.LocalDate

class ActivityRepository(app: Application) {


    private var activityDao: ActivityDao

    init{
        val db = ActivityDatabase.getDB(app)
        activityDao = db.activityDao()
    }


    fun insertBaseActivity(baseActivity: BaseActivity): Long {
        return activityDao.insertBaseActivity(baseActivity)
    }

    fun insertWalkingActivity(walkingActivity: WalkingActivity) {
        return activityDao.insertWalkingActivity(walkingActivity)
    }

    fun insertRunningActivity(runningActivity: RunningActivity) {
        return activityDao.insertRunningActivity(runningActivity)
    }

    fun insertDrivingActivity(drivingActivity: DrivingActivity) {
        return activityDao.insertDrivingActivity(drivingActivity)
    }

    fun insertSittingActivity(sittingActivity: SittingActivity){
        return activityDao.insertSittingActivity(sittingActivity)
    }


    fun getAllActivities(startDate: LocalDate?): LiveData<List<ActivityJoin>> {
        return activityDao.getListOfActivities(startDate)
    }

    fun getAllStepsFromDay(date: LocalDate?): LiveData<Int?> {
        return activityDao.getAllStepsFromDay(date)
    }

    fun getActivitiesFromType(activityType: ActivityType?): LiveData<List<ActivityJoin>> {
        return activityDao.getActivitiesFromType(activityType)
    }

    fun getInfoActivityByID(id: Long?): LiveData<ActivityJoin> {
        return activityDao.getInfoActivityByID(id)
    }


}


