package com.example.progettolam.DB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.UI.homeFragment.HomeViewModel
import java.time.LocalDate
import java.time.YearMonth

class ActivityViewModel(private val repository: ActivityRepository): ViewModel() {

    var currentMonth: YearMonth = YearMonth.now()


    fun getActivities(startDate: LocalDate?): LiveData<List<Activity>> {
        return repository.getAllActivities(startDate)
    }

    fun insertActivity(activity: Activity) {
        repository.insertActivity(activity)
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