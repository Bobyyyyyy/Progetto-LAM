package com.example.progettolam.UI.Activities

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.DrivingActivity
import com.example.progettolam.R
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class OnGoingDriving: OnGoingActivity() {
    private lateinit var speedText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drive_activity)

        viewModel = ViewModelProvider(this)[OnGoingViewModel::class.java]
        viewModel.setActivityType(ActivityType.DRIVING)

        initViews()
        setupListeners()

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

    private fun registerActivity() {
        val id = UUID.randomUUID().toString()
        activityViewModel.insertDrivingActivity(
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
            DrivingActivity(id, viewModel.getAverageSpeed())
        )
    }

    override fun initViews() {
        super.initViews()
        speedText = findViewById(R.id.valueSpeedTextView)
    }
}