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


    fun getAllActivities(): LiveData<List<ActivityJoin>> {
        return activityDao.getListOfActivities()
    }


}


