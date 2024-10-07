package com.example.progettolam.UI.homeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.Activity
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R

class HomeFragment: Fragment() {
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


        addButton.setOnClickListener {
            activityViewModel.insertActivity(Activity(0,"ciao","ciao"))
        }



        val greeting = getString(R.string.greetings) + ", " + storedName
        textView.text = greeting


    }

}