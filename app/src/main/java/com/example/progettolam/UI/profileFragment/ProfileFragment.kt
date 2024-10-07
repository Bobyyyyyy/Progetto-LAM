package com.example.progettolam.UI.profileFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.R
import com.example.progettolam.UI.preferencesActivity.PreferencesFragment
import com.example.progettolam.UI.preferencesActivity.preferencesActivity

class ProfileFragment: Fragment() {
    lateinit var profileModel: ProfileViewModel
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
        val textView: TextView = view.findViewById(R.id.profileText)
        val settings: ImageView= view.findViewById(R.id.settings)
        profileModel.text.observe(viewLifecycleOwner) { 
            textView.text = it
        }

        textView.setOnClickListener {
            profileModel.changeTest()
        }


        settings.setOnClickListener{
            changeFragment(PreferencesFragment(), R.id.settings.toString())
        }


        /*
        settings.setOnClickListener{
            val intent: Intent = Intent(activity,preferencesActivity::class.java)
            startActivity(intent)
        }

         */

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