package com.example.progettolam

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.progettolam.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.tasks.OnFailureListener


class GeofenceMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geoFenceHelper: GeoFenceHelper

    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!

    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE: Int = 100
    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val GEOFENCE_ID = "SOME_GEOFENCE_ID"
    private val GEOFENCE_RADIUS = 500f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //geofencingClient = LocationServices.getGeofencingClient(this)
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
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geoFenceHelper = GeoFenceHelper(requireContext());
    }

    //@SuppressLint("MissingPermission")
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
        mMap.isMyLocationEnabled = true
        //enableUserLocation()
        mMap.setOnMapLongClickListener(this)
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
            //We need background permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(p0);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
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
        _binding = null // Clean up binding when the view is destroyed
    }

    private fun handleMapLongClick(latLng: LatLng) {
        Log.i("diocane", "entra in handleMapLongClick")
        //mMap.clear()
        addMarker(latLng)
        addCircle(latLng, GEOFENCE_RADIUS)
        addGeofence(latLng, GEOFENCE_RADIUS)
    }
// TODO : CAMBIARE IL GEOFENCE_ID DEVE ESSERE UNICO
    private fun addGeofence(latLng: LatLng, radius: Float) {
        val geofence: Geofence = geoFenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = geoFenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent = geoFenceHelper.getPendingIntent()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener { Log.i("funziona", "onSuccess: Geofence Added...") }
            .addOnFailureListener { e ->
                val errorMessage: String = geoFenceHelper.getErrorString(e)
                Log.d("errore", "onFailure: $errorMessage")
            }
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("geo-fence")
        mMap.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        mMap.addCircle(circleOptions)
    }


}
