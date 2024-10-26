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
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.example.progettolam.R
import yuku.ambilwarna.AmbilWarnaDialog;
import androidx.fragment.app.activityViewModels


class GeofenceFragment : Fragment() {
    private val geofenceMapViewModel: GeofenceMapViewModel by activityViewModels()

    private lateinit var mPickColorButton: Button
    private var mColorPreview: View? = null
    private var mDefaultColor = Color.rgb(255, 0, 0)
    private lateinit var radiusEditText: EditText
    private lateinit var removeGeofenceToggleButton: ToggleButton

    val MIN_RADIUS: Int = 100
    val MAX_RADIUS: Int = 2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.geofencing_map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the UI elements
        mPickColorButton = view.findViewById(R.id.changeColorButton)
        radiusEditText = view.findViewById(R.id.radiusLengthEditTextNumberSigned)
        mColorPreview = view.findViewById(R.id.preview_selected_color)
        removeGeofenceToggleButton = view.findViewById(R.id.toggleRemovingGeofenceButton)
        mDefaultColor = Color.rgb(255, 0, 0)

        mPickColorButton.setOnClickListener {
            openColorPickerDialogue()
        }

        // Set up a listener for the radius input
        radiusEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val radius = s?.toString()?.toIntOrNull()
                if (radius != null && radius >= MIN_RADIUS && radius <= MAX_RADIUS) {
                    geofenceMapViewModel.setSelectedRadius(radius)
                } else {
                    radiusEditText.error = "Please enter a value between $MIN_RADIUS and $MAX_RADIUS"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        removeGeofenceToggleButton.setOnCheckedChangeListener { _, isChecked ->
            geofenceMapViewModel.setSelectedToggleRemove(isChecked)
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
                    // Leave this function body as blank, as the dialog automatically closes when clicked on cancel button
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // Change the mDefaultColor to the selected color when OK is clicked
                    mDefaultColor = color
                    val background = mColorPreview?.background
                    if (background is GradientDrawable) {
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
