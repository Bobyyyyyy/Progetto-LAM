package com.example.progettolam

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.progettolam.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                if (p0 == null) {
                    return
                }
                for (location in p0.locations) {
                    Log.i("ciao",location.speed.toString())
                    // Update the camera position on location update
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 40f))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        updateLocationUI()

        mMap.setOnMapClickListener { latLng -> // When clicked on map

            // Initialize marker options
            val markerOptions = MarkerOptions()
            // Set position of marker
            markerOptions.position(latLng)
            // Set title of marker
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
            // Remove all marker
            mMap.clear()
            // Animating to zoom the marker
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            // Add marker on map
            mMap.addMarker(markerOptions)
            }

        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val lastLocation = task.result
                if (lastLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastLocation!!.latitude,
                            lastLocation!!.longitude), 15.toFloat()))

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation!!.latitude,
                        lastLocation!!.longitude), 15.toFloat()))
                     }

                }

                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-34.0,151.0),15.toFloat()))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-34.0,
                    151.0), 15.toFloat()))
                }
            }




                /*
                try {
        if (locationPermissionGranted) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    map?.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        }
    } catch (e: SecurityException) {
        Log.e("Exception: %s", e.message, e)
    }
                 */

        // Add a marker in Sydney and move the camera


/*
        val mountainView = LatLng(37.4, -122.1)

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        val cameraPositionFATAL EX = CameraPosition.Builder()
            .target(mountainView) // Sets the center of the map to Mountain View
            .zoom(17f)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

 */
    }


private fun updateLocationUI() {
    if (mMap == null) {
        return
    }
    try {
        mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, null)

    } catch (e: SecurityException) {
        Log.e("Exception: %s", e.message, e)
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding when the view is destroyed
    }
}
