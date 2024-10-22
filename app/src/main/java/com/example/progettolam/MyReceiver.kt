package com.example.progettolam

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import java.util.logging.Handler


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        val notificationHelper: NotificationHelper = NotificationHelper(context)
        val geofencingEvent = checkNotNull(GeofencingEvent.fromIntent(intent))
        if (geofencingEvent.hasError()) {
            Log.d("TAG", "onReceive: Error receiving geofence event...")
            return
        }
        val geofenceList = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList!!) {
            Log.d("TAG", "onReceive: " + geofence.requestId)
        }
        val transitionType = geofencingEvent.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                android.os.Handler(Looper.getMainLooper()).post{
                    Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
                    notificationHelper.sendHighPriorityNotification(
                        "GEOFENCE_TRANSITION_ENTER", "",
                        MapsActivity::class.java
                    )
                }
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                android.os.Handler(Looper.getMainLooper()).post{
                    Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
                    notificationHelper.sendHighPriorityNotification(
                        "GEOFENCE_TRANSITION_DWELL", "",
                        MapsActivity::class.java
                    )
                }
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                android.os.Handler(Looper.getMainLooper()).post{
                    Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
                    notificationHelper.sendHighPriorityNotification(
                        "GEOFENCE_TRANSITION_EXIT", "",
                        MapsActivity::class.java
                    )
                }
            }
        }
    }
}