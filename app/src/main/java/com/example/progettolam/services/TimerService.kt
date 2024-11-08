package com.example.progettolam.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.progettolam.MainActivity
import com.example.progettolam.R
import java.util.Timer
import java.util.TimerTask

class TimerService: Service() {
    private var timeElapsed: Int = -1
    private var isStopWatchRunning = false
    private var isServiceRunning = false
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    private var updateTimer = Timer()
    private var stopwatchTimer = Timer()
    private val binder = TimerBinder()

    companion object {
        const val CHANNEL_ID = "Timer_Notifications"

        // Intent Extras
        const val TIME_ELAPSED = "TIME_ELAPSED"
        const val IS_TIMER_RUNNING = "IS_TIMER_RUNNING"

        // Intent Actions
        const val STOPWATCH_TICK = "STOPWATCH_TICK"
        const val STOPWATCH_STATUS = "STOPWATCH_STATUS"

    }

    inner class TimerBinder: Binder() {
        fun getService(): TimerService { return this@TimerService}
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        getNotificationManager()
        createChannel()
        super.onCreate()
    }

    fun moveToForeground() {
        if (isStopWatchRunning) {
            startForeground(1,buildNotification())
            updateTimer = Timer()
            updateTimer.schedule(object : TimerTask() {
                override fun run() {
                    updateNotification()
                }
            }, 0, 1000)
        }
        else if (isServiceRunning) {startForeground(1,buildNotification())}
    }

    fun startStopwatch() {
        isStopWatchRunning = true
        isServiceRunning = true
        sendStatus()
        stopwatchTimer = Timer()
        stopwatchTimer.schedule(object : TimerTask() {
            override fun run() {
                val stopwatchIntent = Intent()
                stopwatchIntent.action = STOPWATCH_TICK
                timeElapsed++
                stopwatchIntent.putExtra(TIME_ELAPSED, timeElapsed)
                sendBroadcast(stopwatchIntent)
            }
        }, 0, 1000)
    }

    fun pauseStopwatch() {
        stopwatchTimer.cancel()
        isStopWatchRunning = false
        sendStatus()
    }

    fun moveToBackground() {
        updateTimer.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun deleteTimer() {
        pauseStopwatch()
        isServiceRunning = false
        stopwatchTimer.purge()
        stopForeground(STOP_FOREGROUND_REMOVE)
        updateTimer.cancel()
        updateTimer.purge()
        stopSelf()
    }

    private fun sendStatus() {
        val statusIntent = Intent()
        statusIntent.action = STOPWATCH_STATUS
        statusIntent.putExtra(IS_TIMER_RUNNING, isStopWatchRunning)
        statusIntent.putExtra(TIME_ELAPSED, timeElapsed)
        sendBroadcast(statusIntent)
    }

    /* Notification functions */

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            notificationManagerCompat.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationManager() {
        if (!::notificationManagerCompat.isInitialized) {
            notificationManagerCompat = NotificationManagerCompat.from(this)
        }
    }

    private fun buildNotification(): Notification {
        val title = if (isStopWatchRunning) {
            getString(R.string.OnGoingNotification)
        } else {
            getString(R.string.StopNotification)
        }

        val hours: Int = timeElapsed.div(60).div(60)
        val minutes: Int = timeElapsed.div(60)
        val seconds: Int = timeElapsed.rem(60)

        val intent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText(
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${
                    "%02d".format(
                        seconds
                    )
                }"
            )
            .setColorized(true)
            .setColor(getColor(R.color.primary))
            .setSmallIcon(R.drawable.baseline_person_24)
            .setOnlyAlertOnce(true)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun updateNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(1, buildNotification())
        }
    }
}