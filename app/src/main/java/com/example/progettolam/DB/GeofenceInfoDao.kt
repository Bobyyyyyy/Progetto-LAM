package com.example.progettolam.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GeofenceInfoDao {

    @Query("SELECT * FROM geofence_info ")
    fun getListOfGeofences(): LiveData<List<GeofenceInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGeofence(geofenceInfo: GeofenceInfo)

    @Query("DELETE FROM geofence_info WHERE id = :id")
    fun deleteGeofence(id: String)

}