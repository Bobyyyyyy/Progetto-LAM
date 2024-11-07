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

    private val _imported = MutableLiveData<Boolean>().apply {
        value = false
    }
    val imported: LiveData<Boolean> = _imported

    fun setImported(value: Boolean) {
        _imported.value = value
    }

    private fun insertBaseActivity(baseActivity: BaseActivity) {
        repository.insertBaseActivity(baseActivity)
    }

    fun insertWalkingActivity(baseActivity: BaseActivity, walkingActivity: WalkingActivity) {
            if (baseActivity.activityType == ActivityType.WALKING) {
                CoroutineScope(Dispatchers.IO).launch {
                    insertBaseActivity(baseActivity)
                    repository.insertWalkingActivity(walkingActivity)
                }
            }
    }

    fun insertRunningActivity(baseActivity: BaseActivity, runningActivity: RunningActivity) {
        if(baseActivity.activityType == ActivityType.RUNNING) {
            CoroutineScope(Dispatchers.IO).launch {
                insertBaseActivity(baseActivity)
                repository.insertRunningActivity(runningActivity)
            }
        }
    }

    fun insertDrivingActivity(baseActivity: BaseActivity, drivingActivity: DrivingActivity) {
        if(baseActivity.activityType == ActivityType.DRIVING) {
            CoroutineScope(Dispatchers.IO).launch {
                insertBaseActivity(baseActivity)
                repository.insertDrivingActivity(drivingActivity)
            }
        }
    }

    fun insertSittingActivity(baseActivity: BaseActivity, sittingActivity: SittingActivity) {
        if (baseActivity.activityType == ActivityType.STILL) {
            CoroutineScope(Dispatchers.IO).launch {
                insertBaseActivity(baseActivity)
                repository.insertSittingActivity(sittingActivity)
            }
        }
    }


    fun getAllActivities(startDate: LocalDate?, imported: Boolean): LiveData<List<ActivityJoin>> {
        return repository.getAllActivities(startDate,imported)
    }

    fun getAllStepsFromDay(date: LocalDate?): LiveData<Int?> {
        return repository.getAllStepsFromDay(date)
    }

    fun getInfoActivityByID(id: String?): LiveData<ActivityJoin> {
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

    fun getLastActivityLiveData(): LiveData<ActivityJoin?> {
        return repository.getLastActivityLiveData()
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