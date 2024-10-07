package com.example.progettolam

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.preference.PreferenceManager
import com.example.progettolam.UI.homeFragment.HomeFragment
import com.example.progettolam.UI.profileFragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


// da cambiare la gestione del backstack, vorrei farlo come instagram che quandi clicchi su una sezione ed è già nel backstack riapre quella.


class MainActivity : AppCompatActivity() {
    private lateinit var navigationBar: BottomNavigationView
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val storedTheme = sharedPref?.getBoolean(getString(R.string.preferences_theme),false)

        if (storedTheme == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        fragmentContainer = findViewById(R.id.fragmentContainerView)
        navigationBar = findViewById(R.id.homeNavigation)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                /*
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                val backstackCount = supportFragmentManager.backStackEntryCount
                if(backstackCount > 0 ) {
                    val lastBackStackEntry = supportFragmentManager.getBackStackEntryAt(backstackCount-1)
                    val lastFragment = supportFragmentManager.findFragmentByTag(lastBackStackEntry.name)

                    if(lastFragment != null) {
                        supportFragmentManager.beginTransaction().run {
                            if (currentFragment != null) {
                                detach(currentFragment)
                            }
                            attach(lastFragment)
                            commit()
                        }
                    }

                }

                 */

                supportFragmentManager.popBackStackImmediate()
            }
        }

        onBackPressedDispatcher.addCallback(this,callback)
        navigationBar.setOnItemSelectedListener { menuItem ->
            navbarListener(menuItem)
        }

    }

    private fun navbarListener(menuItem: MenuItem): Boolean {
        when (val id: Int = menuItem.itemId) {
            R.id.homeMenu -> {
                if(!(menuItem.isChecked)) {
                    changeFragment(HomeFragment(),id.toString())
                }
            }
            R.id.calendarMenu -> {
            }
            R.id.profileMenu -> {
                if(!(menuItem.isChecked)) {
                    changeFragment(ProfileFragment(),id.toString())
                }
            }
            R.id.addMenu -> {
            }
            else -> { false }
        }
        return true
    }


    private fun changeFragment(fragment: Fragment, tag: String) {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)

        if (currentFragment != null && currentFragment.tag == tag) {
            return
        }


        val storedFragment = supportFragmentManager.findFragmentByTag(tag)

        if ( storedFragment != null ) {
            supportFragmentManager.beginTransaction().run {
                if (currentFragment != null) {
                    detach(currentFragment)
                }
                attach(storedFragment)
                commit()
            }
        }

        else {

            supportFragmentManager.beginTransaction().run {
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