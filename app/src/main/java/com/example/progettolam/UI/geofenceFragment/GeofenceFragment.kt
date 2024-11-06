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
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.DB.GeofenceRepository
import com.example.progettolam.R
import yuku.ambilwarna.AmbilWarnaDialog


class GeofenceFragment : Fragment() {
    private val geofenceMapViewModel by lazy {
        val factory = GeofenceViewModelFactory(GeofenceRepository(requireActivity().application))
        ViewModelProvider(requireActivity(),factory)[GeofenceMapViewModel::class.java]
    }

    private lateinit var mPickColorButton: Button
    private var mColorPreview: View? = null
    private var mDefaultColor = Color.rgb(255, 0, 0)
    private lateinit var radiusEditText: EditText
    private lateinit var removeGeofenceToggleButton: ToggleButton

    companion object {
        const val MIN_RADIUS: Int = 100
        const val MAX_RADIUS: Int = 2000
    }

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
                    radiusEditText.error = getString(R.string.error_radius)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        removeGeofenceToggleButton.setOnCheckedChangeListener { _, isChecked ->
            geofenceMapViewModel.setSelectedToggleRemove(isChecked)
        }
    }

    private fun openColorPickerDialogue() {
        val colorPickerDialogue = AmbilWarnaDialog(requireContext(), mDefaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    mDefaultColor = color
                    val background = mColorPreview?.background
                    if (background is GradientDrawable) {
                        background.setColor(mDefaultColor)
                    }
                    geofenceMapViewModel.setSelectedColor(mDefaultColor)
                }
            })
        colorPickerDialogue.show()
    }
}
