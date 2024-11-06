package com.example.progettolam.UI.geofenceFragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.GeofenceInfo
import com.example.progettolam.DB.GeofenceRepository
import com.example.progettolam.R
import com.example.progettolam.databinding.ActivityMapsBinding
import com.example.progettolam.services.LocationWorkerScheduler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener


class GeofenceMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geoFenceHelper: GeofenceHelper

    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!

    private val geofenceMapViewModel by lazy {
        val factory = GeofenceViewModelFactory(GeofenceRepository(requireActivity().application))
        ViewModelProvider(requireActivity(),factory)[GeofenceMapViewModel::class.java]
    }

    private var selectedColor: Int = Color.rgb(255, 0, 0) // Default circle color
    private var selectedRadius: Int = 500 // Default circle radius
    private var selectedToggleRemove: Boolean = false

    private var existingGeofence: MutableMap<String, Pair<Circle, Marker?>> = mutableMapOf()

    companion object {
        const val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE: Int = 100
        const val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
        const val ZOOM_LEVEL = 14f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // Initialize Google Maps, FusedLocationProviderClient, and GeofencingClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geoFenceHelper = GeofenceHelper(requireContext());

        // Initialize observers for the geofenceMapViewModel
        geofenceMapViewModel.selectedColor.observe(viewLifecycleOwner) { color ->
            selectedColor = color
        }
        geofenceMapViewModel.selectedRadius.observe(viewLifecycleOwner) { radius ->
            selectedRadius = radius
        }
        geofenceMapViewModel.selectedToggleRemove.observe(viewLifecycleOwner) { isChecked ->
            selectedToggleRemove = isChecked
        }

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    LocationWorkerScheduler(requireContext(),true)
                    mapFragment.getMapAsync(this)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    LocationWorkerScheduler(requireContext(),false)
                    mapFragment.getMapAsync(this)
                } else -> {
            }
            }
        }

        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        mapFragment.getMapAsync(this)
    }

    // Create a unique id using data class
    private fun createGeofenceId(position: LatLng, color: Int, radius: Int): String {
        return GeofenceKey(position, color, radius).toString()
    }

    private fun populateMap(geofenceListFromDb: List<GeofenceInfo>) {
        var position :LatLng
        if (geofenceListFromDb != null) {
            for (geofence in geofenceListFromDb) {
                if (!existingGeofence.containsKey(geofence.id)) {
                    position = LatLng(geofence.latitude, geofence.longitude)
                    //addGeofence(position, geofence.id, geofence.radius)
                    displayGeofence(position, geofence.color, geofence.radius, geofence.id)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Get last known location
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        }

        val locationTask = fusedLocationClient.lastLocation.addOnCompleteListener { location ->
            if (location.isSuccessful) {
                val lastLocation = location.result
                if (lastLocation != null) {
                    val loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                    //mMap.addMarker(MarkerOptions().position(loc).title("Your Current Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM_LEVEL))
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_fetch_location), Toast.LENGTH_SHORT).show();
            }
        }

        locationTask.addOnFailureListener(OnFailureListener {
            Toast.makeText(requireContext(),ContextCompat.getString(requireContext(), R.string.error_fetch_location), Toast.LENGTH_SHORT).show()
        })

        enableUserLocation()

        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener{ marker ->
            handleMarkerClick(marker)
            // false shows the info and relocate the camera, true does nothing
            selectedToggleRemove
        }

        geofenceMapViewModel.getGeofencesDB().observe(requireActivity()) {
            geofenceMapViewModel.setGeofenceInfoList(it)
            populateMap(it)
        }
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            // Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show a dialog for displaying why the permission is needed and then ask for the permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            // Need background permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(p0);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Show a dialog and ask for permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    );
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    );
                }
            }
        } else {
            handleMapLongClick(p0);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleMapLongClick(latLng: LatLng) {
        val newId: String = createGeofenceId(latLng, selectedColor, selectedRadius)
        if (!existingGeofence.containsKey(newId)) {
            addGeofence(latLng, newId)
            displayGeofence(latLng, selectedColor, selectedRadius, newId)
        }
    }

    private fun displayGeofence(latLng: LatLng, color2use: Int, radius2use: Int, geofenceId: String) {
        val newMarker = addMarker(latLng)
        val newCircle = addCircle(latLng, color2use, radius2use)
        // adding the new geofence to the map
        existingGeofence[geofenceId] = Pair(newCircle, newMarker)
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng: LatLng, geofenceId: String) {
        // adding the new geofence to the list in the view model
        geofenceMapViewModel.addGeofenceInfo(GeofenceInfo(geofenceId, latLng.latitude, latLng.longitude, selectedColor, selectedRadius))

        // creating the new geofence based on the latLng, geofenceId and radius
        val geofence: Geofence = geoFenceHelper.getGeofence(
            geofenceId,
            latLng,
            selectedRadius.toFloat(),
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )

        val geofencingRequest: GeofencingRequest = geoFenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent = geoFenceHelper.getPendingIntent()

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.i("funziona", "onSuccess: Geofence Added...")
                Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.success_add_geofence), Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener { e ->
                val errorMessage: String = geoFenceHelper.getErrorString(e)
                Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_add_geofence), Toast.LENGTH_SHORT).show();
                Log.d("errore", "onFailure: $errorMessage")
            }
    }

    private fun addMarker(latLng: LatLng): Marker? {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("$latLng")

        return mMap.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, color2use: Int, radius2use: Int): Circle {
        val circleOptions = CircleOptions()
        .center(latLng)
        .radius(radius2use.toDouble())
        .strokeColor(ColorUtils.setAlphaComponent(color2use, 255)) // Use selected color for stroke
        .fillColor(ColorUtils.setAlphaComponent(color2use, 64)) // Use selected color for fill
        .strokeWidth(4f)

        return mMap.addCircle(circleOptions)
    }

    private fun handleMarkerClick(marker: Marker) {
        if (selectedToggleRemove) {
            val geofenceId2remove = getGeofenceIdByMarkerId(marker.id)
            if (geofenceId2remove == null) {
                Log.i("Error", "geofenceId2remove is null")
                return
            }
           deleteGeofence(geofenceId2remove, marker)
        }
    }

    private fun deleteGeofence(geofenceId2remove: String, marker: Marker) {
        // Removing the geofence from the client
        geofencingClient.removeGeofences(listOf(geofenceId2remove))
            .addOnSuccessListener {
                // Removing the circle and marker from the map
                val circle2remove = getCircleByGeofenceId(geofenceId2remove)
                marker.remove()
                circle2remove?.remove()

                // Removing the geofence from the view model
                geofenceMapViewModel.removeGeofenceInfo(geofenceId2remove)

                // Removing the geofence from the existing geofence list
                existingGeofence.remove(geofenceId2remove)

                // Notify the user the successful removal
                Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.success_geofence_removal), Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener {
                // Notify the user the successful removal
                Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_geofence_removal), Toast.LENGTH_SHORT).show();
            }

    }

    private fun getGeofenceIdByMarkerId(markerId: String) : String? {
        // Iterate through the existing geofences and find the one with the matching markerId
        return existingGeofence.entries.firstOrNull {
            it.value.second?.id == markerId
        }?.key
    }

    private fun getCircleByGeofenceId(geofenceId: String) : Circle? {
        return existingGeofence[geofenceId]?.first
    }
}
