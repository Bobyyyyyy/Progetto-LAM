package com.example.progettolam.UI.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.RunningActivity
import com.example.progettolam.DB.WalkingActivity
import com.example.progettolam.R
import com.example.progettolam.UI.homeFragment.HomeViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class OldActivityInsertFragment: Fragment() {
    private lateinit var spinnerActivity: Spinner
    private lateinit var cancelBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var btnDatePicker: Button
    private lateinit var btnStartTimePicker: Button
    private lateinit var btnEndTimePicker: Button
    private lateinit var txtDate: TextView
    private lateinit var txtStartTime: TextView
    private lateinit var txtEndTime: TextView
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0

    private val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.old_activity_insert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnStartTimePicker = view.findViewById(R.id.selectStartTimeButton)
        btnEndTimePicker = view.findViewById(R.id.selectEndTimeButton)
        btnDatePicker = view.findViewById(R.id.selectDateButton)
        saveBtn = view.findViewById(R.id.saveButton)
        cancelBtn = view.findViewById(R.id.cancelButton)
        txtDate = view.findViewById(R.id.selectDateTextView)
        txtStartTime = view.findViewById(R.id.selectStartTimeTextView)
        txtEndTime = view.findViewById(R.id.selectEndTimeTextView)
        spinnerActivity = view.findViewById(R.id.selectTypeActivitySpinnerActivity)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerActivity.adapter = adapter
        }
        btnDatePicker.setOnClickListener{
            chooseDate()
        }
        btnStartTimePicker.setOnClickListener{
            chooseTime(txtStartTime)
        }
        btnEndTimePicker.setOnClickListener{
            chooseTime(txtEndTime)
        }
        saveBtn.setOnClickListener{
            saveNewActivity()
        }
    }

    private fun chooseDate() {
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                txtDate.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun chooseTime(textView: TextView) {
        val c: Calendar = Calendar.getInstance()
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                textView.text = "$hourOfDay:$minute"
            },
            mHour,
            mMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun getTypeActivity(): String {
        return spinnerActivity.selectedItem.toString()
    }

    private fun getDate(): String {
        return txtDate.text.toString()
    }

    private fun getStartTime(): String {
        return txtStartTime.text.toString()
    }

    private fun getEndTime(): String {
        return txtEndTime.text.toString()
    }

    private fun isIntervalTimeCorrect(): Boolean {
        var isCorrectTime : Boolean = false
        try {
            val startTime = getLocalTimeFromString(txtStartTime)
            val endTime = getLocalTimeFromString(txtEndTime)

            // Check if start time is before end time
            if (startTime != null) {
                isCorrectTime = startTime.isBefore(endTime)
            }
        } catch (e: Exception) {
            isCorrectTime = false
        }
        return isCorrectTime
    }

    private fun getLocalTimeFromString(textView: TextView): LocalTime? {
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
        var localTime: LocalTime? = null
        try {
            localTime = LocalTime.parse(textView.text.toString(), timeFormatter)
        } catch (e: Exception) {
            // Handle parsing error (e.g., if the times are not set correctly)
            Log.i("LocalTime", "Error")
        }
        return localTime
    }

    private fun getLocalDateFromString(textView: TextView): LocalDate? {
        val dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy")
        var localDate: LocalDate? = null
        try {
            localDate = LocalDate.parse(textView.text, dateFormatter)
        } catch (e: Exception) {
            // Handle parsing error (e.g., if the times are not set correctly)
            Log.i("LocalTime", "Error")
        }
        return localDate
    }

    private fun saveNewActivity() {
        val typeActivity = getTypeActivity()

        if (typeActivity != "" && getDate() != "" && getStartTime() != "" && getEndTime() != "" && isIntervalTimeCorrect()) {
            val startTime = getLocalTimeFromString(txtStartTime)
            val endTime = getLocalTimeFromString(txtEndTime)
            val date = getLocalDateFromString(txtDate)

            when (typeActivity) {
                getString(R.string.walk_tag) -> {
                    activityViewModel.insertWalkingActivity(
                        BaseActivity(
                            null,
                            ActivityType.RUNNING,
                            startTime,
                            date,
                            endTime,
                            date
                        ),
                        WalkingActivity(null, null, 0f)
                    )
                }

                getString(R.string.run_tag) -> {
                    activityViewModel.insertRunningActivity(
                        BaseActivity(
                            null,
                            ActivityType.RUNNING,
                            startTime,
                            date,
                            endTime,
                            date
                        ),
                        RunningActivity(null, null, 0f)
                    )
                }

                getString(R.string.chilling_tag) -> {
                    //TODO: Insert Chilling Activity
                }

                getString(R.string.drive_tag) -> {
                    // TODO: Insert Driving Activity
                }
            }
        }
    }
}