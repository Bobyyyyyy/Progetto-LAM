package com.example.progettolam.DB

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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




}

