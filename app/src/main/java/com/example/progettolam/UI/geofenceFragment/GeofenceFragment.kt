package com.example.progettolam.UI.geofenceFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.R
import com.google.android.gms.maps.SupportMapFragment
import yuku.ambilwarna.AmbilWarnaDialog;
import android.widget.TextView;



class GeofenceFragment : Fragment() {

    private val geofenceMapViewModel: GeofenceMapViewModel by viewModels()
    lateinit var viewModel: ViewModel

    private lateinit var mPickColorButton: Button
    private var mColorPreview: View? = null
    private var mDefaultColor = 0

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
        mPickColorButton = view.findViewById(R.id.button)
        mColorPreview = view.findViewById(R.id.preview_selected_color)
        mDefaultColor = 0


        mPickColorButton.setOnClickListener {
            openColorPickerDialogue()
        }

    }

    /**
     * the dialog functionality is handled separately using openColorPickerDialog this is triggered as
     * soon as the user clicks on the Pick Color button And the AmbilWarnaDialog has 2 methods to be overridden
     * those are onCancel and onOk which handle the "Cancel" and "OK" button of color picker dialog
     */
    private fun openColorPickerDialogue() {
        val colorPickerDialogue = AmbilWarnaDialog(requireContext(), mDefaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    // Leave this function body as
                    // blank, as the dialog
                    // automatically closes when
                    // clicked on cancel button
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // Change the mDefaultColor to
                    // the selected color when OK is clicked
                    mDefaultColor = color

                    // Now change the picked color preview box to mDefaultColor
                    mColorPreview?.setBackgroundColor(mDefaultColor)
                }
            })
        colorPickerDialogue.show()
    }
}
