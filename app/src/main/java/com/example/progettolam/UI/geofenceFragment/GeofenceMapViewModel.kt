package com.example.progettolam.UI.geofenceFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.GeofenceInfo
import com.example.progettolam.DB.GeofenceRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceMapViewModel(private val repository: GeofenceRepository) : ViewModel() {
    // MutableLiveData to hold the selected color
    private val _selectedColor = MutableLiveData<Int>()
    val selectedColor: LiveData<Int> get() = _selectedColor

    private val _selectedRadius = MutableLiveData<Int>()
    val selectedRadius: LiveData<Int> get() = _selectedRadius

    private val _selectedToggleRemove = MutableLiveData<Boolean>()
    val selectedToggleRemove: LiveData<Boolean> get() = _selectedToggleRemove

    private val _geofenceInfoList =  MutableLiveData<List<GeofenceInfo>>().apply {
        value = null
    }

    // Function to update the selected color
    fun setSelectedColor(color: Int) {
        _selectedColor.value = color
    }

    fun setSelectedRadius(radius: Int) {
        _selectedRadius.value = radius
    }

    fun setSelectedToggleRemove(isRemoving: Boolean) {
        _selectedToggleRemove.value = isRemoving
    }

    fun setGeofenceInfoList(listGeofence: List<GeofenceInfo>) {
        _geofenceInfoList.value = listGeofence
    }

    fun addGeofenceInfo(newGeofenceInfo: GeofenceInfo) {
        _geofenceInfoList.value = _geofenceInfoList.value?.plus(newGeofenceInfo) ?: listOf(newGeofenceInfo)
        insertGeofenceDB(newGeofenceInfo)
    }

    fun removeGeofenceInfo(geofenceId2remove: String) {
        // Get the current list of geofence
        val currentList = _geofenceInfoList.value?.toMutableList() ?: return

        val itemToRemove = currentList.find { it.id == geofenceId2remove }
        itemToRemove?.let {
            currentList.remove(it)
            // Update the LiveData with the new list
            _geofenceInfoList.value = currentList
        }
        deleteGeofenceDB(geofenceId2remove)
    }


    fun getGeofencesDB(): LiveData<List<GeofenceInfo>> {
        return repository.getListOfGeofences()
    }

    private fun insertGeofenceDB(geofenceInfo: GeofenceInfo?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (geofenceInfo != null) {
                repository.insertGeofence(geofenceInfo)
            }
        }
    }

    private fun deleteGeofenceDB(id: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (id != null) {
                repository.deleteGeofence(id)
            }
        }
    }
}

class GeofenceViewModelFactory(private val repository: GeofenceRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GeofenceMapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeofenceMapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
