package com.example.progettolam.UI.homeFragment

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.OnGoingActivity
import com.example.progettolam.R
import com.example.progettolam.services.TimerService

class HomeFragment: Fragment() {
    private lateinit var timerService: TimerService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){}

     private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerService = (service as TimerService.TimerBinder).getService()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
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

        val textView: TextView = view.findViewById(R.id.textView)
        val addButton: Button = view.findViewById(R.id.addActivity)

        val activityViewModel: ActivityViewModel by viewModels {
            ActivityViewModelFactory(
                ActivityRepository(requireActivity().application)
            )
        }



        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }



        addButton.setOnClickListener {


            /*
              val intent = Intent(requireActivity(),TimerService::class.java)
            intent.putExtra(
                TimerService.TIMER_ACTION,TimerService.START
            )
            requireActivity().startService(intent)


             */
            val intent = Intent(requireActivity(),OnGoingActivity::class.java)
            startActivity(intent)


        }



        val greeting = getString(R.string.greetings) + ", " + storedName
        textView.text = greeting


    }


    override fun onStart() {
        /*
        if (ContextCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val intent = Intent(requireActivity(),TimerService::class.java)
        intent.putExtra(
            TimerService.TIMER_ACTION,TimerService.MOVE_TO_BACKGROUND
        )
        requireActivity().startService(intent)

         */
        super.onStart()
    }

    override fun onPause() {
        /*
        val intent = Intent(requireActivity(),TimerService::class.java)
        intent.putExtra(
            TimerService.TIMER_ACTION,TimerService.MOVE_TO_FOREGROUND
        )
        requireActivity().startService(intent)


         */

        super.onPause()
    }

}