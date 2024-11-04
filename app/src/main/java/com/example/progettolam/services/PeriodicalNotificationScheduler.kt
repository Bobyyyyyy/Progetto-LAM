package com.example.progettolam.services

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

fun PeriodicalNotificationScheduler(context: Context, intervalMinutes: Long) {

    val workRequest = PeriodicWorkRequestBuilder<PeriodicalNotificationWorker>(
        intervalMinutes,
        TimeUnit.MINUTES
    ).setConstraints(
        Constraints.Builder()
            .build()).build()

    // Programma il worker
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        LocationWorker.workName,
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}