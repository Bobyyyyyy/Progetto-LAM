package com.example.progettolam.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.progettolam.DB.ActivityDatabase
import com.example.progettolam.R
import java.time.LocalTime

class PeriodicalNotificationWorker(context: Context, param: WorkerParameters) :
    CoroutineWorker(context, param) {
    companion object {
        const val WORK_NAME = "PeriodicalNotification"
        const val CHANNEL_NAME = "PeriodicalNotification"
        private const val CHANNEL_ID = "Periodical_notification"
    }

    private val db = ActivityDatabase.getDB(context)

    override suspend fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS,) != PackageManager.PERMISSION_GRANTED) {
            return Result.failure()
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val activityDao = db.activityDao()

        val lastActivity  = activityDao.getLastActivity()

        if(lastActivity == null ) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(applicationContext.getString(R.string.no_activity_registered))
                .setContentText(applicationContext.getString(R.string.record_some_activities))
                .setSmallIcon(R.drawable.baseline_person_24)
                .build()

            notificationManager.notify(1001, notification)
        }

        else if(lastActivity.baseActivity.endTime?.isBefore(LocalTime.now().minusMinutes(15)) == true ) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(applicationContext.getString(R.string.you_are_still))
                .setContentText(applicationContext.getString(R.string.record_some_activities))
                .setSmallIcon(R.drawable.baseline_person_24)
                .build()

            notificationManager.notify(1001, notification)
        }

        return Result.success()
    }
}


