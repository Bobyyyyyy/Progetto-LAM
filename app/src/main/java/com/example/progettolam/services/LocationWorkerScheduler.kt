package com.example.progettolam.services

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.work.*
import java.util.concurrent.TimeUnit

fun LocationWorkerScheduler(context: Context, start: Boolean) {


    val workRequest = OneTimeWorkRequestBuilder<LocationWorker>()
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)


    /*
    // Crea un oggetto PeriodicWorkRequest
    val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(
        intervalMinutes, // Specifica l'intervallo in minuti
        TimeUnit.MINUTES
    ).setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Esempio di constraint, se richiesto
            .build()).build()

    // Programma il worker
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        LocationWorker.workName,
        ExistingPeriodicWorkPolicy.KEEP, // Mantieni il lavoro se già esistente
        workRequest
    )

     */
}
