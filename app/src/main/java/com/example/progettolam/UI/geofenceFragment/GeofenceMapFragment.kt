package com.example.progettolam.UI.geofenceFragment

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.progettolam.R
import com.example.progettolam.databinding.ActivityMapsBinding
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

    private val geofenceMapViewModel: GeofenceMapViewModel by activityViewModels() // Shared ViewModel
    private var selectedColor: Int = Color.rgb(255, 0, 0) // Default circle color
    private var selectedRadius: Int = 500 // Default circle radius
    private var selectedToggleRemove: Boolean = false

    val existingGeofences: MutableMap<String, Triple<Geofence, Circle, Marker>> = mutableMapOf()

    companion object {
        const val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE: Int = 100
        const val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
        const val GEOFENCE_ID = "SOME_GEOFENCE_ID"
    }

    private lateinit var circle2remove: Circle

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

        mapFragment.getMapAsync(this)

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
        populateMap()
    }

    private fun populateMap() {
        val geofencesFromDb : List<GeofenceInfo> = geofenceMapViewModel.getGeofencesFromDb()
        var position :LatLng

        for (geofence in geofencesFromDb) {
            position = LatLng(geofence.latitude, geofence.longitude)
            addGeofence(position, geofence.radius)
            displayGeofence(position, geofence.color, geofence.radius)
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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true
                }

            } else {
                Toast.makeText(requireContext(), "Can't Fetch your current location", Toast.LENGTH_SHORT).show();
            }
        }

        locationTask.addOnFailureListener(OnFailureListener {
            Toast.makeText(requireContext(),"Can't Fetch your current location", Toast.LENGTH_SHORT).show()
        })

        enableUserLocation()

        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener{ marker ->
            handleMarkerClick(marker)
            selectedToggleRemove // false shows the info and relocate the camera, true does nothing
        }
        populateMap()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
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
            // We need background permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(p0);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // We show a dialog and ask for permission
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
        addGeofence(latLng, selectedRadius)
        displayGeofence(latLng, selectedColor, selectedRadius)
    }

    private fun displayGeofence(latLng: LatLng, color2use: Int, radius2use: Int) {
        val newMarker = addMarker(latLng)
        val newCircle = addCircle(latLng, color2use, radius2use)
        //existingGeofences[GEOFENCE_ID] = Triple(newMarker, newCircle, newMarker)]

    }

// TODO : CAMBIARE IL GEOFENCE_ID DEVE ESSERE UNICO
    private fun addGeofence(latLng: LatLng, radius2use: Int, existingId: String?) {
        val id2use :String = if (existingId != null) existingId else newId.toString()

        val geofence: Geofence = geoFenceHelper.getGeofence(
            id2use,
            latLng,
            radius2use.toFloat(),
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = geoFenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent = geoFenceHelper.getPendingIntent()

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.i("funziona", "onSuccess: Geofence Added...")
                Toast.makeText(requireContext(), "Geofence added successfully", Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener { e ->
                val errorMessage: String = geoFenceHelper.getErrorString(e)
                Toast.makeText(requireContext(), "Can't add geofence", Toast.LENGTH_SHORT).show();
                Log.d("errore", "onFailure: $errorMessage")
            }
    }

    private fun addMarker(latLng: LatLng): Marker? {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("$latLng")
        return mMap.addMarker(markerOptions)
        /*
        mMap.setOnMarkerClickListener{ marker ->
            /*
            var idMarker = marker.id
            marker.remove()
            circle2remove.remove()
            var geofenceList2remove = ArrayList<String>()
            geofenceList2remove.add(GEOFENCE_ID)
            geofencingClient.removeGeofences(geofenceList2remove)
            */
            handleMarkerClick(marker)
            selectedToggleRemove // false shows the info and relocate the camera, true does nothing
        }
         */
    }

    private fun addCircle(latLng: LatLng, color2use: Int, radius2use: Int): Circle {
        val circleOptions = CircleOptions()
        .center(latLng)
        .radius(radius2use.toDouble())
        .strokeColor(ColorUtils.setAlphaComponent(color2use, 255)) // Use selected color for stroke
        .fillColor(ColorUtils.setAlphaComponent(color2use, 64)) // Use selected color for fill
        .strokeWidth(4f)

        //circle2remove = mMap.addCircle(circleOptions)
        return mMap.addCircle(circleOptions)
        // TODO: SALVARE GLI ID DEI CERCHI E USARLI PER RIMUOVERLI DALLA MAPPA
        //Log.i("cerchioID", "id: ${circle2remove.id}")
    }

    private fun handleMarkerClick(marker: Marker) {
        if (selectedToggleRemove) {
            marker.remove()
            circle2remove.remove()
            geofencingClient.removeGeofences(listOf(getGeofenceIdByMarkerId(marker.id)))
            Toast.makeText(requireContext(), "Geofence removed successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private fun getGeofenceIdByMarkerId(markerId: String) : String {
        //TODO: get the geofence id by the marker id
        return GEOFENCE_ID
    }
}
