package com.example.progettolam.UI.geofenceFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.R
import com.google.android.gms.maps.SupportMapFragment

class GeofenceFragment : Fragment() {

    private val geofenceMapViewModel: GeofenceMapViewModel by viewModels()
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.geofencing_map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        // Dynamically load GeofenceMapFragment into the FrameLayout (R.id.map_container)
        val geofenceMapFragment = GeofenceMapFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, geofenceMapFragment)
            .commit()

         */

        /*
        // ViewModel usage, etc.
        viewModel = ViewModelProvider(this)[GeofenceMapViewModel::class.java]
        // Now, you can observe any data or actions from the ViewModel shared with GeofenceMapFragment
        geofenceMapViewModel.geofenceLocation.observe(viewLifecycleOwner) { latLng ->
            // Use the location data (update the UI, pass it to the map fragment, etc.)
        }
        */

    }
}
