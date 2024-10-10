package com.example.progettolam.DB

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

class ActivityRepository(app: Application) {


    private var activityDao: ActivityDao

    init{
        val db = ActivityDatabase.getDB(app)
        activityDao = db.activityDao()
    }


    fun insertActivity(activity: Activity) {
        ActivityDatabase.scope.launch {
            activityDao.insert(activity)
        }
    }

    fun getAllActivities(): LiveData<List<Activity>> {
        return activityDao.getListOfActivities(startTime = LocalTime.now())
    }


}


