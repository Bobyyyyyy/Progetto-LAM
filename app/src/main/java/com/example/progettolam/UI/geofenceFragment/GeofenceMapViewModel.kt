package com.example.progettolam.UI.geofenceFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class GeofenceMapViewModel : ViewModel() {

    // TODO : gestire il salvataggio e rimozione dei geofence

    // LiveData for geofence-related events like geofence location, errors, etc.
    private val _geofenceLocation = MutableLiveData<LatLng>()
    val geofenceLocation: LiveData<LatLng> = _geofenceLocation

    private val _geofenceError = MutableLiveData<String>()
    val geofenceError: LiveData<String> = _geofenceError


    // Function to update geofence location (e.g. from user input)
    fun setGeofenceLocation(latLng: LatLng) {
        _geofenceLocation.value = latLng
    }

    // Function to handle and propagate geofence errors
    fun setGeofenceError(error: String) {
        _geofenceError.value = error
    }
}
