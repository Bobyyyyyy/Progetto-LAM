package com.example.progettolam.services

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

fun LocationWorkerScheduler(context: Context, start: Boolean) {
    val workRequest = OneTimeWorkRequestBuilder<LocationWorker>()
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
}
