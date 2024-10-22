package com.example.progettolam.UI.Activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.progettolam.DB.ActivityType
import java.time.LocalDate
import java.time.LocalTime


class OnGoingViewModel: ViewModel() {

    var startTime: LocalTime? = null
    var endTime: LocalTime? = null
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null


    private val _timeElapsed = MutableLiveData<Int>().apply {
        value = 0
    }
    val timeElapsed: LiveData<Int> = _timeElapsed

    fun changeTime(int: Int) {
        _timeElapsed.value = int
    }


    private val _isBound = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isBound: LiveData<Boolean> = _isBound

    fun setisBound(value: Boolean) {
        _isBound.value = value
    }

    private val _totalSteps = MutableLiveData<Float>().apply {
        value = 0f
    }

    val totalSteps: LiveData<Float> = _totalSteps

    fun setTotalSteps(value: Float) {
        _totalSteps.value = value
    }

    fun getTotalSteps() : Float? {
        return _totalSteps.value
    }

    private val _activityType = MutableLiveData<ActivityType>().apply {
        value = null
    }

    val activityType : LiveData<ActivityType> = _activityType

    fun setActivityType(value: ActivityType) {
        _activityType.value = value
    }

    fun getActivityType(): ActivityType? {
        return _activityType.value
    }



    private val _speedList = MutableLiveData<MutableList<Float>>(mutableListOf())
    val speedList: LiveData<MutableList<Float>> get() = _speedList

    fun addSpeed(value: Float) {
        val currentSpeedList = _speedList.value ?: mutableListOf()
        currentSpeedList.add(value)
        Log.d("SpeedViewModel", "Aggiungo velocità: $value")
        _speedList.postValue(currentSpeedList)
        Log.d("SpeedViewModel", "Nuova lista velocità: ${_speedList.value}")
    }

    fun getAverageSpeed(): Float {
        val speedList = _speedList.value
        if (speedList.isNullOrEmpty()) {
            return 0f
        }

        val sum = speedList.sum()
        return sum / speedList.size
    }



}
