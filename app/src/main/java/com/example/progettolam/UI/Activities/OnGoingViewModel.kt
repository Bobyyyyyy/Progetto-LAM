package com.example.progettolam.UI.Activities

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

    private val _isBound = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isBound: LiveData<Boolean> = _isBound

    private val _totalSteps: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 0
    }
    val totalSteps: LiveData<Int> = _totalSteps

    private val _activityType = MutableLiveData<ActivityType>().apply {
        value = null
    }
    val activityType : LiveData<ActivityType> = _activityType

    private val _speedList = MutableLiveData<MutableList<Float>>(mutableListOf())
    val speedList: LiveData<MutableList<Float>> get() = _speedList

    private val _isStarted = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isStarted : LiveData<Boolean> = _isStarted

    private val _isPaused = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isPaused : LiveData<Boolean> = _isPaused

    fun changeTime(int: Int) {
        _timeElapsed.value = int
    }

    fun setIsBound(value: Boolean) {
        _isBound.value = value
    }

    fun setTotalSteps(value: Int) {
        _totalSteps.value = value
    }

    fun getTotalSteps(): Int? {
        return _totalSteps.value
    }

    fun setActivityType(value: ActivityType) {
        _activityType.value = value
    }

    fun getActivityType(): ActivityType? {
        return _activityType.value
    }

    fun setStarted(value: Boolean) {
        _isStarted.value = value
    }

    fun setPaused(value: Boolean) {
        _isPaused.value = value
    }

    fun addSpeed(value: Float) {
        val currentSpeedList = _speedList.value ?: mutableListOf()
        currentSpeedList.add(value)
        _speedList.postValue(currentSpeedList)
    }

    fun getAverageSpeed(): Float {
        val speedList = _speedList.value
        if (speedList.isNullOrEmpty()) {
            return 0f
        }
        return speedList.sum() / speedList.size
    }
}
