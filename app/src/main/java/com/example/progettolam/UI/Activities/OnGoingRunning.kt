package com.example.progettolam.UI.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.RunningActivity
import com.example.progettolam.R
import com.example.progettolam.services.StepCounter
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


class OnGoingRunning : OnGoingActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted) {
            startActivity()
        } else {
            super.startActivity()
        }
    }

    private lateinit var stepsCounter: TextView
    private lateinit var speedText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recording_stats_with_map_activity)

        viewModel = ViewModelProvider(this)[OnGoingViewModel::class.java]
        viewModel.setActivityType(ActivityType.RUNNING)

        initViews()
        setupListeners()
        viewModel.totalSteps.observe(this) {
            stepsCounter.text = it.toInt().toString()
        }

        endButton.setOnClickListener {
            viewModel.endDate = LocalDate.now()
            viewModel.endTime = LocalTime.now()
            endActivity()

            if(timeElapsed != 0) {
                registerActivity()
            }
            finish()
        }

        viewModel.speedList.observe(this) {
            if(it.isNotEmpty()) {
                speedText.text = it.last().toString()
            }
        }
    }

    override fun initViews() {
        super.initViews()
        stepsCounter = findViewById(R.id.stepsCounter)
        speedText = findViewById(R.id.valueSpeedTextView)
    }

    override fun onResume() {
        super.onResume()
        registerStepReceiver()
        if(isBound) {
            resumeStepsSensor()
        }
    }

    override fun onPause() {
        super.onPause()
        if(!isChangingConfigurations) {
            unregisterReceiver(stepReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            endSteps()
        }
    }

    override fun startActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            super.startActivity()
            if(isPaused) {
                resumeStepsSensor()
            } else {
                startSteps()
            }
        }
    }

    override fun stopActivity() {
        super.stopActivity()
        stopSensor()
    }

    override fun endActivity() {
        super.endActivity()
        endSteps()
    }

    private fun stopSensor() {
        val intent = Intent(this,StepCounter::class.java)
        intent.putExtra(StepCounter.STEP_ACTION, StepCounter.STOP_SENSOR)
        startService(intent)
    }

    private fun startSteps() {
        val intent = Intent(this,StepCounter::class.java)
        intent.putExtra(StepCounter.STEP_ACTION, StepCounter.START_SENSOR)
        startService(intent)
    }

    private fun endSteps() {
        val intent = Intent(this,StepCounter::class.java)
        stopService(intent)
    }

    private fun resumeStepsSensor() {
        val intent = Intent(this,StepCounter::class.java)
        intent.putExtra(StepCounter.STEP_ACTION, StepCounter.RESUME_SENSOR)
        startService(intent)
    }

    private fun registerActivity() {
        val id = UUID.randomUUID().toString()
        activityViewModel.insertRunningActivity(
            BaseActivity(
                id,
                false,
                storedName,
                viewModel.getActivityType(),
                viewModel.startTime,
                viewModel.startDate,
                viewModel.startTime?.plusSeconds(timeElapsed.toLong()),
                viewModel.endDate
            ),
            RunningActivity(id, viewModel.getTotalSteps(), viewModel.getAverageSpeed())
        )
    }
}