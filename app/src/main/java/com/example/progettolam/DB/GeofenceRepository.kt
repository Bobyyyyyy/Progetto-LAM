package com.example.progettolam.DB

import android.app.Application
import androidx.lifecycle.LiveData

class GeofenceRepository(app: Application) {
    private var geofenceDao: GeofenceInfoDao

    init {
        val db = ActivityDatabase.getDB(app)
        geofenceDao = db.geofenceDao()
    }

    fun getListOfGeofences(): LiveData<List<GeofenceInfo>> {
        return geofenceDao.getListOfGeofences()
    }

    fun insertGeofence(geofenceInfo: GeofenceInfo) {
        geofenceDao.insertGeofence(geofenceInfo)
    }

    fun deleteGeofence(id: String) {
        return geofenceDao.deleteGeofence(id)
    }
}