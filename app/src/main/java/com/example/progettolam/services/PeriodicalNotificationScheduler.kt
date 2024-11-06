package com.example.progettolam.services

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun PeriodicalNotificationScheduler(context: Context, intervalMinutes: Long) {
    val workRequest = PeriodicWorkRequestBuilder<PeriodicalNotificationWorker>(
        intervalMinutes,
        TimeUnit.MINUTES
    ).setConstraints(
        Constraints.Builder()
            .build()).build()

    // Program the worker
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        PeriodicalNotificationWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}