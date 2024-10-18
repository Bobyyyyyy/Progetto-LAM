package com.example.progettolam.DB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ActivityViewModel(private val repository: ActivityRepository): ViewModel() {

    var currentMonth: YearMonth = YearMonth.now()

    private fun insertBaseActivity(baseActivity: BaseActivity): Long {
            return repository.insertBaseActivity(baseActivity)
    }


    fun insertWalkingActivity(baseActivity: BaseActivity, walkingActivity: WalkingActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = insertBaseActivity(baseActivity)
            walkingActivity.activityId = id
            repository.insertWalkingActivity(walkingActivity)
        }
    }

    fun insertRunningActivity(baseActivity: BaseActivity, runningActivity: RunningActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = insertBaseActivity(baseActivity)
            runningActivity.activityId = id
            repository.insertRunningActivity(runningActivity)
        }
    }

    fun insertDrivingActivity(baseActivity: BaseActivity, drivingActivity: DrivingActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = insertBaseActivity(baseActivity)
            drivingActivity.activityId = id
            repository.insertDrivingActivity(drivingActivity)
        }
    }

    fun insertSittingActivity(baseActivity: BaseActivity, sittingActivity: SittingActivity) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = insertBaseActivity(baseActivity)
            sittingActivity.activityId = id
            repository.insertSittingActivity(sittingActivity)
        }
    }




    fun getAllActivites(): LiveData<List<ActivityJoin>> {
        return repository.getAllActivities()
    }



}


class ActivityViewModelFactory(private val repository: ActivityRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}