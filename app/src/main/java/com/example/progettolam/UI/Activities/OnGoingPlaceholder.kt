package com.example.progettolam.UI.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType

class OnGoingPlaceholder: AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){}

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

                val intent = Intent(this, OnGoingDriving::class.java)

                startActivity(intent)
            }

            ActivityType.STILL.toString() -> {

                val intent = Intent(this, OnGoingResting::class.java)

                startActivity(intent)
            }

        }

        if (ContextCompat.checkSelfPermission
                (this , Manifest.permission.ACTIVITY_RECOGNITION) !=
            PackageManager.PERMISSION_GRANTED) {
            requestActivityRecognitionPermission()
        }


        finish()
    }


    fun requestActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {

            AlertDialog.Builder(this)
                .setTitle("Permesso di Riconoscimento delle Attività")
                .setMessage("Per fornirti una migliore esperienza, l'app necessita del riconoscimento delle attività fisiche. In questo modo possiamo personalizzare le funzionalità in base al tuo livello di attività.")
                .setPositiveButton("Concedi") { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
                .setNegativeButton("Annulla", null)
                .create()
                .show()
        }
    }
}