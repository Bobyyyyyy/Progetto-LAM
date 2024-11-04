package com.example.progettolam.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.progettolam.DB.ActivityDatabase
import com.example.progettolam.MainActivity
import com.example.progettolam.R
import java.time.LocalTime

class PeriodicalNotificationWorker(context: Context, param: WorkerParameters) :
    CoroutineWorker(context, param) {
    companion object {
        val workName = "PeriodicalNotification"
        private const val TAG = "PeriodicalNotificationWorker"
        private const val CHANNEL_ID = "Periodical_notification"
        private const val NOTIFICATION_ID = 3
    }

    private val db = ActivityDatabase.getDB(context)

    override suspend fun doWork(): Result {

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(CHANNEL_ID, "Periodical Notificationn", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)



        val activityDao = db.activityDao()

        val lastActivity  = activityDao.getLastActivity()



        if(lastActivity == null ) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Non hai registrato ancora attività")
                .setContentText("Registra la tua prima attività")
                .setSmallIcon(R.drawable.baseline_person_24)
                .build()

            notificationManager.notify(1001, notification)
        }

        else if(lastActivity.baseActivity.endTime?.isBefore(LocalTime.now().minusMinutes(15)) == true ) {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Sei fermo da un po'!")
                .setContentText("Registra delle attività")
                .setSmallIcon(R.drawable.baseline_person_24)
                .build()

            notificationManager.notify(1001, notification)
        }





        return Result.success()
    }

}


