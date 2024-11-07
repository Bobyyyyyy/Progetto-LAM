package com.example.progettolam.UI.homeFragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.Activities.OnGoingPlaceholder
import com.example.progettolam.services.LocationWorkerScheduler
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class HomeFragment: Fragment() {
    private lateinit var chairButton: ImageButton
    private lateinit var walkButton: ImageButton
    private lateinit var runButton: ImageButton
    private lateinit var driveButton: ImageButton
    private lateinit var valueStepToday: TextView
    private lateinit var valueIntervalTimeLastActTextView: TextView
    private lateinit var typeLastActTextView: TextView
    private lateinit var typeLastActImageView: ImageView

    private val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                LocationWorkerScheduler(requireContext(),true)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                LocationWorkerScheduler(requireContext(),false)
            } else -> {}
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        walkButton = view.findViewById(R.id.walkButton)
        runButton = view.findViewById(R.id.runButton)
        driveButton = view.findViewById(R.id.driveButton)
        chairButton = view.findViewById(R.id.chairButton)
        valueStepToday = view.findViewById(R.id.valueWalkTextView)
        valueIntervalTimeLastActTextView = view.findViewById(R.id.timeIntervalTextView)
        typeLastActTextView = view.findViewById(R.id.typeLastActTextView)
        typeLastActImageView = view.findViewById(R.id.imgLastAct)

        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission()
        } else if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        else {
            LocationWorkerScheduler(requireContext(),true)
        }

        walkButton.setOnClickListener { activityButtonListener(it)}
        driveButton.setOnClickListener { activityButtonListener(it)}
        runButton.setOnClickListener { activityButtonListener(it)}
        chairButton.setOnClickListener { activityButtonListener(it)}

        activityViewModel.getAllStepsFromDay(LocalDate.now()).observe(viewLifecycleOwner) { step ->
            valueStepToday.text = step?.toString() ?: "0"
        }

        activityViewModel.getLastActivityLiveData().observe(viewLifecycleOwner) { lastActivity ->
            if (lastActivity != null) {
                typeLastActTextView.text = getFormattedTypeActivity(lastActivity.baseActivity.activityType!!)
                val duration = calculateDuration(lastActivity.baseActivity.startDate!!, lastActivity.baseActivity.startTime!!, lastActivity.baseActivity.endDate!!, lastActivity.baseActivity.endTime!!)
                valueIntervalTimeLastActTextView.text = getFormattedTimeInterval(duration)
                updateImageLastActivity(lastActivity.baseActivity.activityType!!)
            }
        }
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

    private fun updateImageLastActivity(type: ActivityType) {
        when (type) {
            ActivityType.WALKING -> typeLastActImageView.setImageResource(R.drawable.baseline_chair_24)
            ActivityType.RUNNING -> typeLastActImageView.setImageResource(R.drawable.baseline_directions_run_24)
            ActivityType.DRIVING -> typeLastActImageView.setImageResource(R.drawable.baseline_drive_eta_24)
            ActivityType.STILL -> typeLastActImageView.setImageResource(R.drawable.baseline_chair_24)
        }
        typeLastActImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary))
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

    private fun activityButtonListener(view: View) {
        val intent = Intent(requireActivity(), OnGoingPlaceholder::class.java)

        when(view.id) {
            R.id.walkButton -> intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.WALKING.toString())
            R.id.runButton -> intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.RUNNING.toString())
            R.id.chairButton -> intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.STILL.toString())
            R.id.driveButton -> intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.DRIVING.toString())
        }
        startActivity(intent)
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Show an explanatory dialog before asking for permission
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.enable_notification))
                .setMessage(getString(R.string.enable_notification_description))
                .setPositiveButton(getString(R.string.approve)) { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton(getString(R.string.cancel_button), null)
                .create()
                .show()
        }
    }
}