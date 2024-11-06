package com.example.progettolam.services


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LocationWorker(context: Context, param: WorkerParameters) :
    CoroutineWorker(context, param) {

    override suspend fun doWork(): Result {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val serviceIntent = Intent(applicationContext, LocationForegroundService::class.java)
        applicationContext.startForegroundService(serviceIntent)

        return Result.success()
    }
}
