package com.example.progettolam

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.DrivingActivity
import com.example.progettolam.DB.RunningActivity
import com.example.progettolam.services.StepCounter
import com.example.progettolam.services.TimerService
import java.time.LocalDate
import java.time.LocalTime

class OnGoingActivity: AppCompatActivity() {

    private lateinit var statusReceiver: BroadcastReceiver
    private lateinit var timeReceiver: BroadcastReceiver
    private lateinit var stepReceiver: BroadcastReceiver
    private lateinit var timerService: TimerService
    private lateinit var timer: TextView
    private lateinit var stepsCounter: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var endButton: Button
    private lateinit var viewModel: OnGoingViewModel
    private var isBound: Boolean = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){}

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerService = (service as TimerService.TimerBinder).getService()
            viewModel.setisBound(true)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.setisBound(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivingIntent: Intent = intent

        if (receivingIntent != null) {
            val type = receivingIntent.getStringExtra(ActivityString.ACTIVITY_TYPE)

            when(type) {
                ActivityString.WALKING, ActivityString.RUNNING -> {
                    setContentView(R.layout.recording_stats_with_map_activity)
                }

                ActivityString.DRIVING -> {
                    setContentView(R.layout.recording_stats_with_map_activity)
                    //da cambiare con altri layout
                }

                ActivityString.STILL -> {
                    setContentView(R.layout.recording_stats_with_map_activity)
                    //da cambiare con altri layout
                }
            }
        }

        initViews()


        val activityViewModel: ActivityViewModel by viewModels {
            ActivityViewModelFactory(
                ActivityRepository(this.application)
            )
        }


        viewModel = ViewModelProvider(this)[OnGoingViewModel::class.java]



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

        endButton.setOnClickListener {
            viewModel.endDate = LocalDate.now()
            viewModel.endTime = LocalTime.now()
            endActivity()

            activityViewModel.insertDrivingActivity(
                BaseActivity(
                null,
                    ActivityType.DRIVING,
                viewModel.startTime,
                viewModel.startDate,
                viewModel.endTime,
                viewModel.endDate
                ),
                DrivingActivity(null))

        }

        viewModel.isBound.observe(this) {
            isBound = it
        }

        viewModel.timeElapsed.observe(this) {
            updateStopwatchValue(it)
        }

        viewModel.totalSteps.observe(this) {
            stepsCounter.text = it.toString()
        }

    }


    override fun onStart() {
        super.onStart()
        if (isBound) {
            timerService.moveToBackground()
        }
        else {
            bindService(Intent(this,TimerService::class.java), serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
     //   getTimerStatus()
        registerTimeReceiver()
        registerStepReceiver()

    }



    override fun onPause() {
        super.onPause()
        if(!isChangingConfigurations) {
            timerService.moveToForeground()
            unregisterReceiver(timeReceiver)
            unregisterReceiver(stepReceiver)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations) {
            endActivity()
            unbindService(serviceConnection)
        }
    }



    private fun initViews() {
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        endButton = findViewById(R.id.endButton)
        timer = findViewById(R.id.timer)
        stepsCounter = findViewById(R.id.stepsCounter)
    }



    private fun startActivity() {
        timerService.startStopwatch()
        val intent = Intent(this,StepCounter::class.java)
        startService(intent)

    }

    private fun stopActivity() {
        timerService.pauseStopwatch()
    }

    private fun endActivity() {
        stopActivity()
        timerService.deleteTimer()
        val intent = Intent(this,StepCounter::class.java)
        stopService(intent)
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
        registerReceiver(timeReceiver,timeFilter)

    }

    private fun registerStepReceiver() {
        val stepFilter = IntentFilter()
        stepFilter.addAction(StepCounter.STEP_STATUS)
        stepReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                viewModel.setTotalSteps(intent?.getFloatExtra(StepCounter.TOTAL_STEPS,0f)!!)
            }
        }

        registerReceiver(stepReceiver,stepFilter)
    }


}