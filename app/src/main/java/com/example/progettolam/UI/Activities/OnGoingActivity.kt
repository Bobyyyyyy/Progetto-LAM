package com.example.progettolam.UI.Activities

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.RunningActivity
import com.example.progettolam.R
import com.example.progettolam.services.StepCounter
import com.example.progettolam.services.TimerService
import java.time.LocalDate
import java.time.LocalTime

open class OnGoingActivity: AppCompatActivity() {

    protected lateinit var statusReceiver: BroadcastReceiver
    protected lateinit var timeReceiver: BroadcastReceiver
    protected lateinit var stepReceiver: BroadcastReceiver
    protected lateinit var timerService: TimerService
    protected lateinit var timer: TextView
    protected lateinit var startButton: Button
    protected lateinit var pauseButton: Button
    protected lateinit var endButton: Button
    protected lateinit var viewModel: OnGoingViewModel
    protected var isBound: Boolean = false

    val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(this.application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerService = (service as TimerService.TimerBinder).getService()
            viewModel.setisBound(true)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.setisBound(false)
        }
    }


    override fun onStart() {
        super.onStart()
        if (isBound) {
            timerService.moveToBackground()
        }
        else {
            val service = Intent(this,TimerService::class.java)
            startService(service)
            bindService(service, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
     //   getTimerStatus()
        registerTimeReceiver()
    }



    override fun onPause() {
        super.onPause()
        if(!isChangingConfigurations) {
            timerService.moveToForeground()
            unregisterReceiver(timeReceiver)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations) {
            endActivity()
            unbindService(serviceConnection)
        }
    }



    protected open fun startActivity() {
        timerService.startStopwatch()

    }

    protected open fun stopActivity() {
        timerService.pauseStopwatch()
    }

    protected open fun endActivity() {
        stopActivity()
        timerService.deleteTimer()
    }

    private fun getTimerStatus() {
        /*
        val stopwatchService = Intent(this, TimerService::class.java)
        stopwatchService.putExtra(TimerService.TIMER_ACTION, TimerService.GET_STATUS)
        startService(stopwatchService)

         */
    }

    private fun updateStopwatchValue(timeElapsed: Int) {
        val hours: Int = (timeElapsed / 60) / 60
        val minutes: Int = timeElapsed / 60
        val seconds: Int = timeElapsed % 60
        val time = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
        timer.text = time
    }


    private fun registerTimeReceiver() {

        val timeFilter = IntentFilter()
        timeFilter.addAction(TimerService.STOPWATCH_TICK)
        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.changeTime(intent?.getIntExtra(TimerService.TIME_ELAPSED,0)!!)
            }
        }
        registerReceiver(timeReceiver,timeFilter, RECEIVER_EXPORTED)

    }

    protected fun registerStepReceiver() {
        val stepFilter = IntentFilter()
        stepFilter.addAction(StepCounter.STEP_STATUS)
        stepReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.setTotalSteps(intent?.getFloatExtra(StepCounter.TOTAL_STEPS,0f)!!)
            }
        }

        registerReceiver(stepReceiver,stepFilter,RECEIVER_EXPORTED)
    }


    private fun registerActivity(activityType: ActivityType?) {

        when(activityType) {
            ActivityType.WALKING -> {
                viewModel.setActivityType(ActivityType.WALKING)
                setContentView(R.layout.recording_stats_with_map_activity)

                activityViewModel.insertRunningActivity(
                    BaseActivity(
                        null,
                        viewModel.getActivityType(),
                        viewModel.startTime,
                        viewModel.startDate,
                        viewModel.endTime,
                        viewModel.endDate
                    ),
                    RunningActivity(null, viewModel.getTotalSteps()?.toInt())
                )

            }

            ActivityType.RUNNING -> {


            }

            ActivityType.DRIVING -> {
                viewModel.setActivityType(ActivityType.DRIVING)
                setContentView(R.layout.recording_stats_with_map_activity)
                //da cambiare con altri layout
            }

            ActivityType.STILL -> {
                viewModel.setActivityType(ActivityType.STILL)
                setContentView(R.layout.recording_stats_with_map_activity)
                //da cambiare con altri layout
            }

            null -> return
        }
    }

    protected open fun initViews() {
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        endButton = findViewById(R.id.endButton)
        timer = findViewById(R.id.timer)
    }

    protected fun setupListeners() {
        startButton.setOnClickListener {
            viewModel.startTime = LocalTime.now()
            viewModel.startDate = LocalDate.now()
            startActivity()
            startButton.visibility = View.INVISIBLE
        }

        pauseButton.setOnClickListener {
            stopActivity()
            startButton.visibility = View.VISIBLE
        }


        viewModel.isBound.observe(this) {
            isBound = it
        }

        viewModel.timeElapsed.observe(this) {
            updateStopwatchValue(it)
        }
    }



}