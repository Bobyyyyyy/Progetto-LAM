package com.example.progettolam.UI.profileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.progettolam.DB.ActivityRepository
import com.example.progettolam.DB.ActivityViewModel
import com.example.progettolam.DB.ActivityViewModelFactory
import com.example.progettolam.R
import com.example.progettolam.UI.preferencesActivity.PreferencesFragment
import java.time.LocalDate

class ProfileFragment: Fragment() {
    private lateinit var profileModel: ProfileViewModel
    private lateinit var textView: TextView
    private lateinit var settings: ImageView
    private lateinit var todaySteps: TextView


    private val activityViewModel by lazy {
        val factory = ActivityViewModelFactory(ActivityRepository(requireActivity().application))
        ViewModelProvider(this, factory)[ActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        textView = view.findViewById(R.id.username)
        settings = view.findViewById(R.id.settings)
        todaySteps = view.findViewById(R.id.todaySteps)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        val value = resources.getString(R.string.preferences_username)
        val storedName = sharedPref?.getString(getString(R.string.preferences_username), value)

        profileModel.changeUsername(storedName.toString())

        profileModel.username.observe(viewLifecycleOwner) {
            textView.text = it
        }

        activityViewModel.getAllStepsFromDay(LocalDate.now()).observe(viewLifecycleOwner) {
            todaySteps.text = it.toString()
        }



        settings.setOnClickListener{
            changeFragment(PreferencesFragment(), R.id.settings.toString())
        }


    }


    private fun changeFragment(fragment: Fragment, tag: String) {

        val currentFragment = parentFragmentManager.findFragmentById(R.id.fragmentContainerView)

        if (currentFragment != null && currentFragment.tag == tag) {
            return
        }


        val storedFragment = parentFragmentManager.findFragmentByTag(tag)

        if ( storedFragment != null ) {
            parentFragmentManager.beginTransaction().run {
                if (currentFragment != null) {
                    detach(currentFragment)
                }
                attach(storedFragment)
                commit()
            }
        }

        else {

            parentFragmentManager.beginTransaction().run {
                setReorderingAllowed(true)
                if (currentFragment != null) {
                    detach(currentFragment)
                }
                add(R.id.fragmentContainerView,fragment,tag)
                addToBackStack(null)
                commit()
            }
        }


    }
}