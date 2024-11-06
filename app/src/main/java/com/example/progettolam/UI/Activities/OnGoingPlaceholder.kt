package com.example.progettolam.UI.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.R

class OnGoingPlaceholder: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivingIntent: Intent = intent

        val type = receivingIntent.getStringExtra(ActivityString.ACTIVITY_TYPE)
        when (type) {
            ActivityType.WALKING.toString() -> {
                startActivity(Intent(this, OnGoingWalking::class.java))
            }

            ActivityType.RUNNING.toString() -> {
                startActivity(Intent(this, OnGoingRunning::class.java))
            }

            ActivityType.DRIVING.toString() -> {
                startActivity(Intent(this, OnGoingDriving::class.java))
            }

            ActivityType.STILL.toString() -> {
                startActivity(Intent(this, OnGoingResting::class.java))
            }
        }
        finish()
    }
}
