package com.example.progettolam.UI.geofenceFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.progettolam.MainActivity
import com.example.progettolam.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceReceiver : BroadcastReceiver() {

    // Called when the BroadcastReceiver is receiving an Intent broadcast.
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper: NotificationHelper = NotificationHelper(context)
        val geofencingEvent = checkNotNull(GeofencingEvent.fromIntent(intent))

        if (geofencingEvent.hasError()) {
            Log.d("Error", "onReceive: Error receiving geofence event...")
            return
        }

        val transitionType = geofencingEvent.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                android.os.Handler(Looper.getMainLooper()).post{
                    Toast.makeText(
                        context,
                        ContextCompat.getString(context, R.string.geofence_enter),
                        Toast.LENGTH_SHORT
                    ).show()
                    notificationHelper.sendHighPriorityNotification(
                        ContextCompat.getString(context, R.string.geofence_enter), "",
                        MainActivity::class.java
                    )
                }
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                android.os.Handler(Looper.getMainLooper()).post{
                    Toast.makeText(context, ContextCompat.getString(context, R.string.geofence_dwell), Toast.LENGTH_SHORT).show()
                    notificationHelper.sendHighPriorityNotification(
                        ContextCompat.getString(context, R.string.geofence_dwell), "",
                        MainActivity::class.java
                    )
                }
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                android.os.Handler(Looper.getMainLooper()).post{
                    Toast.makeText(context, ContextCompat.getString(context, R.string.geofence_exit), Toast.LENGTH_SHORT).show()
                    notificationHelper.sendHighPriorityNotification(
                        ContextCompat.getString(context, R.string.geofence_exit), "",
                        MainActivity::class.java
                    )
                }
            }
        }
    }
}