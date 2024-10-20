package com.example.progettolam.UI.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.R

class OnGoingPlaceholder: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivingIntent: Intent = intent

        val type = receivingIntent.getStringExtra(ActivityString.ACTIVITY_TYPE)
        when(type) {
            ActivityType.WALKING.toString() -> {
                val intent = Intent(this,OnGoingWalking::class.java)

                startActivity(intent)
            }

            ActivityType.RUNNING.toString() -> {
                val intent = Intent(this,OnGoingRunning::class.java)

                startActivity(intent)
            }

            ActivityType.DRIVING.toString() -> {
            }

            ActivityType.STILL.toString() -> {
            }

        }

        finish()
    }

}