package com.example.progettolam.UI.geofenceFragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.R
import com.google.android.gms.maps.SupportMapFragment
import yuku.ambilwarna.AmbilWarnaDialog;
import android.widget.TextView;
import androidx.fragment.app.activityViewModels


class GeofenceFragment : Fragment() {
    private val geofenceMapViewModel: GeofenceMapViewModel by activityViewModels()
    //private val geofenceMapViewModel: GeofenceMapViewModel by viewModels()
    //lateinit var viewModel: ViewModel

    private lateinit var mPickColorButton: Button
    private var mColorPreview: View? = null
    private var mDefaultColor = Color.rgb(255, 0, 0)
    private lateinit var radiusTextView: EditText

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
        radiusTextView = view.findViewById(R.id.editTextNumberSigned)
        mColorPreview = view.findViewById(R.id.preview_selected_color)
        mDefaultColor = Color.rgb(255, 0, 0)

        mPickColorButton.setOnClickListener {
            openColorPickerDialogue()
        }

        // Set up a listener for the radius input
        radiusTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val radius = s?.toString()?.toIntOrNull()
                if (radius != null && radius >= 100 && radius <= 2000) {  // Ensure it's a positive number
                    geofenceMapViewModel.setSelectedRadius(radius)  // Update the ViewModel with selected radius
                    Log.i("Raggio2", "raggio: $radius")
                } else {
                    // Handle invalid input (e.g., show an error message if needed)
                    radiusTextView.error = "Please enter a value between 100 and 2000"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                    // Leave this function body as blank, as the dialog automatically closes when clicked on cancel button
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // Change the mDefaultColor to the selected color when OK is clicked
                    mDefaultColor = color
                    val background = mColorPreview?.background
                    if (background is GradientDrawable) {
                        // change the picked color preview box to mDefaultColor
                        background.setColor(mDefaultColor)
                    }
                    // Update the selected color in the ViewModel
                    geofenceMapViewModel.setSelectedColor(mDefaultColor)
                    Log.i("COLORE_GEO", "selectedColor: $mDefaultColor")
                }
            })
        colorPickerDialogue.show()
    }
}
