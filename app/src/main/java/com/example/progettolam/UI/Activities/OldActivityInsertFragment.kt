package com.example.progettolam.UI.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.DrivingActivity
import com.example.progettolam.DB.RunningActivity
import com.example.progettolam.DB.SittingActivity
import com.example.progettolam.DB.WalkingActivity
import com.example.progettolam.R
import java.time.LocalDate
import java.time.LocalTime

import java.time.format.DateTimeFormatter
import java.util.UUID


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
    private lateinit var storedName: String

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

        var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        var value = resources.getString(R.string.preferences_username_default)
        storedName = sharedPref.getString(getString(R.string.preferences_username), value).toString()

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
        btnDatePicker.setOnClickListener {
            chooseDate()
        }
        btnStartTimePicker.setOnClickListener {
            chooseTime(txtStartTime)
        }
        btnEndTimePicker.setOnClickListener {
            chooseTime(txtEndTime)
        }
        saveBtn.setOnClickListener {
            handleSaveOldActivity()
        }
        cancelBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
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
                if (minute < 10) {
                    textView.text = "$hourOfDay:0$minute"
                } else {
                    textView.text = "$hourOfDay:$minute"
                }
            },
            mHour,
            mMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun getTypeActivity() : String {
        return spinnerActivity.selectedItem.toString()
    }

    private fun getDate() : String {
        return txtDate.text.toString()
    }

    private fun getStartTime() : String {
        return txtStartTime.text.toString()
    }

    private fun getEndTime() : String {
        return txtEndTime.text.toString()
    }

    private fun isIntervalTimeCorrect() : Boolean {
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

    private fun getLocalTimeFromString(textView: TextView) : LocalTime? {
        var localTime: LocalTime? = null
        try {
            val newTime = "${textView.text}:15.1234567"
            localTime = LocalTime.parse(newTime)
        } catch (e: Exception) {
            Log.i("LocalTime", "Error: $e")
        }
        return localTime
    }

    private fun getLocalDateFromString(textView: TextView) : LocalDate? {
        val dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy")
        var localDate: LocalDate? = null
        try {
            localDate = LocalDate.parse(textView.text, dateFormatter)
        } catch (e: Exception) {
            Log.i("LocalTime", "Error")
        }
        return localDate
    }

    // Checks if there are any errors and shows a toast if there are
    private fun areFieldsCorrect() : Boolean {
        var isCorrectTime : Boolean = true
        val typeActivity = getTypeActivity()
        if (typeActivity == "" || getDate() == "" || getStartTime() == "" || getEndTime() == "") {
            isCorrectTime = false
            Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_empty_fields), Toast.LENGTH_LONG).show();
        } else if (!isIntervalTimeCorrect()) {
            isCorrectTime = false
            Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_time_interval), Toast.LENGTH_LONG).show();
        } else if (!isDateAndTimeValid()) {
            isCorrectTime = false
            Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_date), Toast.LENGTH_LONG).show();
        }
        return isCorrectTime
    }

    // Checks if the data is valid: the data inserted is before today and if it is today than it has to check the current time
    private fun isDateAndTimeValid() : Boolean {
        val date = getLocalDateFromString(txtDate)
        val today = LocalDate.now()
        var isDataCorrect: Boolean = false

        if (date != null) {
            isDataCorrect = date.isBefore(today)
            if (date.isEqual(today)) {
                val endTime = getLocalTimeFromString(txtEndTime)
                val currentTime = LocalTime.now()
                if (endTime != null) {
                    isDataCorrect = endTime.isBefore(currentTime)
                }
            }
        }
        return isDataCorrect
    }

    private fun saveNewActivity() {
        val typeActivity = getTypeActivity()
        val startTime: LocalTime? = getLocalTimeFromString(txtStartTime)
        val endTime: LocalTime? = getLocalTimeFromString(txtEndTime)

        val date = getLocalDateFromString(txtDate)
        val id = UUID.randomUUID().toString()
        when (typeActivity) {
            getString(R.string.walk_tag) -> {
                activityViewModel.insertWalkingActivity(
                    BaseActivity(
                        id,
                        false,
                        storedName,
                        ActivityType.WALKING,
                        startTime,
                        date,
                        endTime,
                        date
                    ),
                    WalkingActivity(id, null, null)
                )
            }

            getString(R.string.run_tag) -> {
                activityViewModel.insertRunningActivity(
                    BaseActivity(
                        id,
                        false,
                        storedName,
                        ActivityType.RUNNING,
                        startTime,
                        date,
                        endTime,
                        date
                    ),
                    RunningActivity(id, null, null)
                )
            }

            getString(R.string.chilling_tag) -> {
                activityViewModel.insertSittingActivity(
                    BaseActivity(
                        id,
                        false,
                        storedName,
                        ActivityType.STILL,
                        startTime,
                        date,
                        endTime,
                        date
                    ),
                    SittingActivity(id)
                )
            }

            getString(R.string.drive_tag) -> {
                activityViewModel.insertDrivingActivity(
                    BaseActivity(
                        id,
                        false,
                        storedName,
                        ActivityType.DRIVING,
                        startTime,
                        date,
                        endTime,
                        date
                    ),
                    DrivingActivity(id,null)
                )
            }
        }
    }

    private fun handleSaveOldActivity() {
        if (areFieldsCorrect()) {
            saveNewActivity()
            Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.add_old_activity_success), Toast.LENGTH_SHORT).show();
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }
}