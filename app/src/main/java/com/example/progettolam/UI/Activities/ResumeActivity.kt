package com.example.progettolam.UI.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityJoin
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
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

        val timeIntervalFormatter = DateTimeFormatter.ofPattern("H:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.getDefault())

        activityViewModel.getInfoActivityByID(idActivity?.toLong()).observe(requireActivity()) {
            infoActivity = it

            typeActivity.text = getFormattedTypeActivity(infoActivity?.baseActivity?.activityType!!)

            val formattedStartTime = infoActivity?.baseActivity?.startTime?.format(timeIntervalFormatter)
            val formattedEndTime = infoActivity?.baseActivity?.endTime?.format(timeIntervalFormatter)
            dateActivity.text = infoActivity?.baseActivity?.startDate?.format(dateFormatter).toString().replaceFirstChar {
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
}