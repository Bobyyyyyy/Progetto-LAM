package com.example.progettolam

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.UI.Activities.OnGoingViewModel
import com.example.progettolam.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt


class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var viewModel: OnGoingViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!
    private var currentSpeed = 0.0f
    private var previousPosition: LatLng = LatLng(0.0, 0.0)
    private var isStarted = false

    companion object {
        const val SPEED_THRESHOLD = 4.0f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[OnGoingViewModel::class.java]
        viewModel.isStarted.observe(requireActivity()) {
            isStarted = it
        }

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
                    val speed = ms2kmh(location.speed)
                    if (roundCoordinates(location.latitude, location.longitude) != roundCoordinates(previousPosition.latitude,previousPosition.longitude)) {
                        if (speed >= SPEED_THRESHOLD && speed != currentSpeed) {
                            previousPosition = LatLng(location.latitude,location.longitude)
                            currentSpeed = speed
                            if(isStarted) {
                                viewModel.addSpeed(currentSpeed)
                            }
                        }
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude), 15f
                            )
                        )
                    } else {
                        if (currentSpeed != 0f) {
                            currentSpeed = 0f
                            if(isStarted) {
                                viewModel.addSpeed(currentSpeed)
                            }
                        }
                    }
                }
            }
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(2500)
            .build()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        updateLocationUI()

        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val lastLocation = task.result
                previousPosition = LatLng(lastLocation.latitude, lastLocation.longitude)
                currentSpeed = ms2kmh(lastLocation.speed)
                if (lastLocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastLocation.latitude,
                            lastLocation.longitude), 15.toFloat()))

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), 15.toFloat()))
                }
            } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-34.0,151.0),15.toFloat()))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-34.0,
                    151.0), 15.toFloat()))
            }
        }
    }

    private fun updateLocationUI() {
        if (mMap == null) {
            return
        }
        try {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    // Convert the speed from m/s to km/h
    private fun ms2kmh (value: Float): Float {
        val speedInKm = value * 3.6f
        Log.i("SpeedViewModel", value.toString())
        return (speedInKm * 10).roundToInt() / 10.0f
    }

    private fun roundCoordinates(latitude: Double, longitude: Double): LatLng {
        return LatLng(
            ((latitude * 100000).roundToInt() / 100000.0f).toDouble(),
            ((longitude * 100000).roundToInt() / 100000.0f).toDouble()
        )
    }

    override fun onResume() {
        super.onResume()
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        } catch (e: Exception) {
            Log.e("Exception: %s", e.message, e )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _binding = null // Clean up binding when the view is destroyed
    }
}
