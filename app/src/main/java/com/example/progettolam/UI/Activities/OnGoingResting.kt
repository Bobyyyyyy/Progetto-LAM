package com.example.progettolam.UI.Activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.SittingActivity
import com.example.progettolam.R
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class OnGoingResting: OnGoingActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_activity)

        viewModel = ViewModelProvider(this)[OnGoingViewModel::class.java]
        viewModel.setActivityType(ActivityType.STILL)

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
    }

    private fun registerActivity() {
        val id = UUID.randomUUID().toString()
        activityViewModel.insertSittingActivity(
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
            SittingActivity(id)
        )
    }
}