package com.example.progettolam.UI.homeFragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityString
import com.example.progettolam.DB.ActivityType
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.Activities.OnGoingPlaceholder
import com.example.progettolam.services.LocationWorkerScheduler
import java.time.LocalDate

class HomeFragment: Fragment() {
    private lateinit var chairButton: ImageButton
    private lateinit var walkButton: ImageButton
    private lateinit var runButton: ImageButton
    private lateinit var driveButton: ImageButton
    private lateinit var valueStepToday: TextView
    private lateinit var valueTimeToday: TextView
    private lateinit var typeLastActTextView: TextView
    private lateinit var typeLastActImageView: ImageView

    private val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                LocationWorkerScheduler(requireContext(),true)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                LocationWorkerScheduler(requireContext(),false)
            } else -> {
        }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if(isGranted) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        walkButton = view.findViewById(R.id.walkButton)
        runButton = view.findViewById(R.id.runButton)
        driveButton = view.findViewById(R.id.driveButton)
        chairButton = view.findViewById(R.id.chairButton)
        valueStepToday = view.findViewById(R.id.valueWalkTextView)
        valueTimeToday = view.findViewById(R.id.valueTimerTextView)
        typeLastActTextView = view.findViewById(R.id.typeLastActTextView)
        typeLastActImageView = view.findViewById(R.id.imgLastAct)

        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission()
        } else if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        walkButton.setOnClickListener { activityButtonListener(it)}
        driveButton.setOnClickListener { activityButtonListener(it)}
        runButton.setOnClickListener { activityButtonListener(it)}
        chairButton.setOnClickListener { activityButtonListener(it)}

        activityViewModel.getAllStepsFromDay(LocalDate.now()).observe(viewLifecycleOwner) { step ->
            valueStepToday.text = step?.toString() ?: "0"
        }
        activityViewModel.getLastActivityLiveData().observe(viewLifecycleOwner) { lastActivity ->
            if (lastActivity != null) {
                typeLastActTextView.text = getFormattedTypeActivity(lastActivity.baseActivity.activityType!!)
                updateImageLastActivity(lastActivity.baseActivity.activityType!!)
            }
        }
    }

    private fun updateImageLastActivity(type: ActivityType) {
        when (type) {
            ActivityType.WALKING -> typeLastActImageView.setImageResource(R.drawable.baseline_chair_24)
            ActivityType.RUNNING -> typeLastActImageView.setImageResource(R.drawable.baseline_directions_run_24)
            ActivityType.DRIVING -> typeLastActImageView.setImageResource(R.drawable.baseline_drive_eta_24)
            ActivityType.STILL -> typeLastActImageView.setImageResource(R.drawable.baseline_chair_24)
        }
        typeLastActImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary))
    }

    private fun getFormattedTypeActivity(type: ActivityType): String {
        val formattedType = when (type) {
            ActivityType.WALKING -> getString(R.string.walk_tag)
            ActivityType.RUNNING -> getString(R.string.run_tag)
            ActivityType.DRIVING -> getString(R.string.drive_tag)
            ActivityType.STILL -> getString(R.string.chilling_tag)
        }
        return formattedType
    }

    private fun activityButtonListener(view: View) {
        val intent = Intent(requireActivity(), OnGoingPlaceholder::class.java)

        when(view.id) {
            R.id.walkButton -> {
                intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.WALKING.toString())
            }
            R.id.runButton -> {
                intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.RUNNING.toString())
            }
            R.id.chairButton -> {
                intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.STILL.toString())
            }
            R.id.driveButton -> {
                intent.putExtra(ActivityString.ACTIVITY_TYPE, ActivityType.DRIVING.toString())
            }
        }
        startActivity(intent)
    }


    fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            // Mostra un dialogo esplicativo prima di chiedere il permesso
            AlertDialog.Builder(requireContext())
                .setTitle("Attiva le Notifiche")
                .setMessage("Abilitando le notifiche, possiamo inviarti notifiche periodiche e aggiornarti sulle attività in corso.")
                .setPositiveButton("Concedi") { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton("Annulla", null)
                .create()
                .show()
        }
    }
}