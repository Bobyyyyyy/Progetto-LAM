package com.example.progettolam.UI.Activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.BaseActivity
import com.example.progettolam.DB.RunningActivity
import com.example.progettolam.MapsActivity
import com.example.progettolam.R
import com.example.progettolam.services.StepCounter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import java.time.LocalDate
import java.time.LocalTime


class OnGoingWalking : OnGoingActivity() {

    private lateinit var stepsCounter: TextView
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recording_stats_with_map_activity)

        viewModel = ViewModelProvider(this)[OnGoingViewModel::class.java]
        viewModel.setActivityType(ActivityType.WALKING)

        initViews()
        setupListeners()
        viewModel.totalSteps.observe(this) {
            stepsCounter.text = it.toString()
        }

        endButton.setOnClickListener {
            viewModel.endDate = LocalDate.now()
            viewModel.endTime = LocalTime.now()
            endActivity()

            registerActivity()

        }

        /*
                supportFragmentManager.beginTransaction().run {
                    setReorderingAllowed(true)
                    replace(R.id.map,MapsActivity(),"ciao")
                    addToBackStack(null)
                    commit()


         */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //va messo prima o messo l'accesso nella onresult sennò crasha
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun initViews() {
        super.initViews()
        stepsCounter = findViewById(R.id.stepsCounter)
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
        super.startActivity()
        startSteps()
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


}