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

    // MutableLiveData to hold the selected color
    private val _selectedColor = MutableLiveData<Int>()
    val selectedColor: LiveData<Int> get() = _selectedColor

    private val _selectedRadius = MutableLiveData<Int>()
    val selectedRadius: LiveData<Int> get() = _selectedRadius

    // Function to update geofence location (e.g. from user input)
    fun setGeofenceLocation(latLng: LatLng) {
        _geofenceLocation.value = latLng
    }

    // Function to handle and propagate geofence errors
    fun setGeofenceError(error: String) {
        _geofenceError.value = error
    }


    // Function to update the selected color
    fun setSelectedColor(color: Int) {
        _selectedColor.value = color
    }

    fun setSelectedRadius(radius: Int) {
        _selectedRadius.value = radius
    }
}
