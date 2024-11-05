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

class HomeFragment: Fragment() {

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
        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val defaultShared = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val value = resources.getString(R.string.preferences_username)
        val storedName = defaultShared?.getString(getString(R.string.preferences_username), value)

        val walkButton: ImageButton = view.findViewById(R.id.walkButton)
        val bikeButton: ImageButton = view.findViewById(R.id.runButton)
        val driveButton: ImageButton = view.findViewById(R.id.driveButton)
        val chairButton: ImageButton = view.findViewById(R.id.chairButton)


        val activityViewModel: ActivityViewModel by viewModels {
            ActivityViewModelFactory(
                ActivityRepository(requireActivity().application)
            )
        }


        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission()
        }
        else if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        walkButton.setOnClickListener { activityButtonListener(it)}
        driveButton.setOnClickListener { activityButtonListener(it)}
        bikeButton.setOnClickListener { activityButtonListener(it)}
        chairButton.setOnClickListener { activityButtonListener(it)}


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
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

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