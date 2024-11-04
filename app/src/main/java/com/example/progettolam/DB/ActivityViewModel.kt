package com.example.progettolam.DB

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.UI.profileFragment.ActivitiesGraphData
import com.example.progettolam.UI.profileFragment.StepsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ActivityViewModel(private val repository: ActivityRepository): ViewModel() {

    var currentMonth: YearMonth = YearMonth.now()

    var selectedDate: LocalDate? = LocalDate.now()

    private val _id = MutableLiveData<Long>().apply {
        value = null
    }

    val id: LiveData<Long> = _id

    private fun changeId(value: Long) {
        _id.value = value
    }

    fun returnId(): Long? {
        return _id.value
    }

    private fun insertBaseActivity(baseActivity: BaseActivity): Long {
            return repository.insertBaseActivity(baseActivity)
    }


    fun insertWalkingActivity(baseActivity: BaseActivity, walkingActivity: WalkingActivity) {
        if (baseActivity.activityType == ActivityType.WALKING) {
            CoroutineScope(Dispatchers.IO).launch {
                val id = insertBaseActivity(baseActivity)
                walkingActivity.activityId = id
                repository.insertWalkingActivity(walkingActivity)

            }
        }
    }

    fun insertRunningActivity(baseActivity: BaseActivity, runningActivity: RunningActivity) {
        if(baseActivity.activityType == ActivityType.RUNNING) {
            CoroutineScope(Dispatchers.IO).launch {
                val id = insertBaseActivity(baseActivity)
                runningActivity.activityId = id
                repository.insertRunningActivity(runningActivity)
            }
        }
    }

    fun insertDrivingActivity(baseActivity: BaseActivity, drivingActivity: DrivingActivity) {
        if(baseActivity.activityType == ActivityType.DRIVING) {
            CoroutineScope(Dispatchers.IO).launch {
                val id = insertBaseActivity(baseActivity)
                drivingActivity.activityId = id
                repository.insertDrivingActivity(drivingActivity)
            }
        }
    }

    fun insertSittingActivity(baseActivity: BaseActivity, sittingActivity: SittingActivity) {
        if (baseActivity.activityType == ActivityType.STILL) {
            CoroutineScope(Dispatchers.IO).launch {
                val id = insertBaseActivity(baseActivity)
                sittingActivity.activityId = id
                repository.insertSittingActivity(sittingActivity)
            }
        }
    }


    fun getAllActivities(startDate: LocalDate?, imported: Boolean = true): LiveData<List<ActivityJoin>> {
        return repository.getAllActivities(startDate,imported)
    }

    fun getAllStepsFromDay(date: LocalDate?): LiveData<Int?> {
        return repository.getAllStepsFromDay(date)
    }

    fun getAllActivities(activityType: ActivityType?): LiveData<List<ActivityJoin>> {
        return repository.getActivitiesFromType(activityType)
    }

    fun getInfoActivityByID(id: Long?): LiveData<ActivityJoin> {
        return repository.getInfoActivityByID(id)
    }
    fun getCurrentWeekSteps(today: LocalDate?) : LiveData<Array<StepsData>> {
        return repository.getCurrentWeekSpinner(today)
    }

    fun getCurrentWeekActivities(today: LocalDate?): LiveData<Array<ActivitiesGraphData>> {
        return repository.getCurrentWeekActivities(today)
    }

    fun exportDBtoCSV(context: Context, uri: Uri) {
        return repository.exportActivitiesToCSV(uri,context)
    }

    fun importCSVtoDB(context: Context, uri: Uri) {
        return repository.importActivitiesFromCSV(uri,context)
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