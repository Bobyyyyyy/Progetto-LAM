package com.example.progettolam.services

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.work.*
import java.util.concurrent.TimeUnit

fun LocationWorkerScheduler(context: Context, start: Boolean) {


    val workRequest = OneTimeWorkRequestBuilder<PeriodicalNotificationWorker>()
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)

}
