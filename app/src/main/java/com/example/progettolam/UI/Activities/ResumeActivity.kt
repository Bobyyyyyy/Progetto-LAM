package com.example.progettolam.UI.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityJoin
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
// private var idActivity: String
class ResumeActivity(): Fragment() {
    private var idActivity: String? = null
    private lateinit var typeActivity: TextView
    private lateinit var dateActivity: TextView
    private lateinit var timeIntervalActivity: TextView
    private lateinit var valueAvSpeed: TextView
    private lateinit var valueTotalCalories: TextView
    private lateinit var valueTotalSteps: TextView
    private lateinit var valueTotalTime: TextView
    private lateinit var valueTotalDistance: TextView
    private lateinit var valueUser: TextView
    private var infoActivity: ActivityJoin? = null

    private val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        idActivity = arguments?.getString("idActivity")
        return inflater.inflate(R.layout.show_deatil_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeActivity = view.findViewById(R.id.typeActivityTextView)
        dateActivity = view.findViewById(R.id.dateTextView)
        timeIntervalActivity = view.findViewById(R.id.intervalTimeTextView)
        valueAvSpeed = view.findViewById(R.id.valueAvSpeedTextView)
        valueTotalCalories = view.findViewById(R.id.totalCaloriesValueTextView)
        valueTotalSteps = view.findViewById(R.id.totalStepsValueTextView)
        valueTotalTime = view.findViewById(R.id.totalTimeValueTextView)
        valueTotalDistance = view.findViewById(R.id.valueTotalDistanceTextView)
        valueUser = view.findViewById(R.id.valueUserTextView)

        val timeIntervalFormatter = DateTimeFormatter.ofPattern("H:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.getDefault())

        activityViewModel.getInfoActivityByID(idActivity?.toLong()).observe(requireActivity()) {
            infoActivity = it

            typeActivity.text = getFormattedTypeActivity(infoActivity?.baseActivity?.activityType!!)

            val startTime = infoActivity?.baseActivity?.startTime
            val endTime = infoActivity?.baseActivity?.endTime
            val startDate = infoActivity?.baseActivity?.startDate
            val endDate = infoActivity?.baseActivity?.endDate

            if (startDate != null && startTime != null && endDate != null && endTime != null) {
                val duration = calculateDuration(startDate, startTime, endDate, endTime)
                valueTotalTime.text = getFormattedTimeInterval(duration)
                getOtherInfo(duration)
            } else {
                valueTotalTime.text = "--:--:--"
            }

            val formattedStartTime = startTime?.format(timeIntervalFormatter)
            val formattedEndTime = endTime?.format(timeIntervalFormatter)
            dateActivity.text = startDate?.format(dateFormatter).toString().replaceFirstChar {
                char -> char.titlecase()
            }

            val timeInterval = "$formattedStartTime-$formattedEndTime"
            timeIntervalActivity.text = timeInterval

        }

    }

    private fun getFormattedTypeActivity(type: ActivityType): String {
        val formattedType = when (type) {
            ActivityType.WALKING -> getString(R.string.walk_tag)
            ActivityType.RUNNING -> getString(R.string.run_tag)
            ActivityType.DRIVING -> getString(R.string.drive_tag)
            ActivityType.STILL -> getString(R.string.chilling_tag)
        }
        return formattedType
    }

    private fun calculateDuration(startDate: LocalDate, startTime: LocalTime, endDate: LocalDate, endTime: LocalTime): Duration {
        val start: LocalDateTime = LocalDateTime.of(startDate, startTime)
        val end: LocalDateTime = LocalDateTime.of(endDate, endTime)
        return Duration.between(start,end)
    }

    private fun getFormattedTimeInterval(duration: Duration) : String {
        val days = duration.toDays().toInt()
        val hours = (duration.toHours() % 24).toInt()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        val time = if (days != 0) {
            "${"%02d".format(days)}:${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
        } else {
            // If days are zero, exclude them
            "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
        }
        return time
    }

    private fun getOtherInfo(duration: Duration) {
        when (infoActivity?.baseActivity?.activityType) {
            ActivityType.WALKING -> {
                if (infoActivity?.walkingActivity?.avgSpeed != null) {
                    valueAvSpeed.text = getFormattedAvgSpeed(infoActivity?.walkingActivity?.avgSpeed!!)
                    valueTotalDistance.text = getFormattedDistance(getDistanceInKm(infoActivity?.walkingActivity?.avgSpeed!!, duration))
                    getCalories(ActivityType.WALKING, duration, infoActivity?.walkingActivity?.avgSpeed!!)
                }
                if (infoActivity?.walkingActivity?.steps != null) {
                    valueTotalSteps.text = infoActivity?.walkingActivity?.steps.toString()
                }
            }
            ActivityType.RUNNING -> {
                if (infoActivity?.runningActivity?.avgSpeed != null) {
                    valueAvSpeed.text = getFormattedAvgSpeed(infoActivity?.runningActivity?.avgSpeed!!)
                    valueTotalDistance.text = getFormattedDistance(getDistanceInKm(infoActivity?.runningActivity?.avgSpeed!!, duration))
                    getCalories(ActivityType.RUNNING, duration, infoActivity?.runningActivity?.avgSpeed!!)
                }
                if (infoActivity?.runningActivity?.steps != null) {
                    valueTotalSteps.text = infoActivity?.runningActivity?.steps.toString()
                }
            }
            ActivityType.DRIVING -> {
                if (infoActivity?.drivingActivity?.avgSpeed != null) {
                    valueTotalDistance.text = getFormattedDistance(getDistanceInKm(infoActivity?.drivingActivity?.avgSpeed!!, duration))
                    valueAvSpeed.text = getFormattedAvgSpeed(infoActivity?.drivingActivity?.avgSpeed!!)
                }
            }
            else -> {}
        }
    }

    private fun getFormattedDistance(distance: Double): String {
        return "${"%.3f".format(distance)} km/h"
    }

    private fun getDistanceInKm(avgSpeed: Float, duration: Duration): Double {
        return (avgSpeed.toDouble())*duration.toHours()
    }

    private fun getFormattedAvgSpeed(avgSpeed: Float): String {
        return "${"%.2f".format(avgSpeed)} km/h"
    }

    private fun getCalories(type: ActivityType, duration: Duration, avgSpeed: Float) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val storedWeight = sharedPref?.getString(getString(R.string.preferences_weight), null)
        if (storedWeight != null) {
            val totalCalories = calculateCaloriesBurnt(type, storedWeight.toInt(), duration, avgSpeed)
            valueTotalCalories.text = totalCalories.toString()
        }
    }

    private fun calculateCaloriesBurnt(type: ActivityType, weight: Int, duration: Duration, avgSpeed: Float) : Int {
        val coefficient = if (type == ActivityType.RUNNING) { 0.9 } else { 0.48 }
        // Distance in km
        val distance : Double = getDistanceInKm(avgSpeed, duration)
        val calories = coefficient*distance*weight.toDouble()
        return calories.toInt()
    }
}