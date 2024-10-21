package com.example.progettolam.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.progettolam.services.TimerService.Companion.TIMER_ACTION

class StepCounter : SensorEventListener, Service(){

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    companion object {
        const val STEP_ACTION = "STEP_ACTION"
        const val STEP_STATUS = "STEP_STATUS"
        const val TOTAL_STEPS = "TOTAL_STEPS"
        const val START_SENSOR = "START_SENSOR"
        const val STOP_SENSOR = "STOP_SENSOR"
        const val RESUME_SENSOR = "RESUME_SENSOR"
        const val END_SENSOR = "END_SENSOR"
    }

    inner class StepBinder: Binder() {
        fun getService(): StepCounter {return this@StepCounter}
    }

    private var totalSteps = 0f

    private val binder = StepBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val action = intent?.getStringExtra(STEP_ACTION)!!


        when (action) {
            START_SENSOR -> startSensor()
            STOP_SENSOR -> stopSensor()
            RESUME_SENSOR -> resumeSensor()
        }


        return START_STICKY
    }


    private fun startSensor() {
        if(stepCounterSensor == null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            //controllo se il dispositivo non ha il sensore
            sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

    }

    private fun sendSteps() {
        if(stepCounterSensor != null) {
            val stepsIntent = Intent()
            stepsIntent.action = STEP_STATUS
            stepsIntent.putExtra(TOTAL_STEPS, totalSteps)
            sendBroadcast(stepsIntent)
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            totalSteps++
            sendSteps()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Non è necessario implementare nulla qui per questa app
    }


    override fun onDestroy() {
        super.onDestroy()
        totalSteps = 0f
        stepCounterSensor = null
        stopSelf()
    }

    private fun stopSensor() {
        sensorManager.unregisterListener(this)
    }

    private fun resumeSensor() {
        if(stepCounterSensor != null) {
            sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

}
