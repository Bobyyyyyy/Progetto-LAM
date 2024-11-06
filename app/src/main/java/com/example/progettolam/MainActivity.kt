package com.example.progettolam

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.preference.PreferenceManager
import com.example.progettolam.UI.calendarFragment.CalendarFragment
import com.example.progettolam.UI.geofenceFragment.GeofenceFragment
import com.example.progettolam.UI.homeFragment.HomeFragment
import com.example.progettolam.UI.profileFragment.ProfileFragment
import com.example.progettolam.services.PeriodicalNotificationScheduler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView


class MainActivity : AppCompatActivity() {
    private lateinit var fragmentContainer: FragmentContainerView
    private var navigationRail: NavigationRailView? = null
    private var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val storedTheme = sharedPref?.getBoolean(getString(R.string.preferences_theme),false)

        if (storedTheme == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.main_activity)
        fragmentContainer = findViewById(R.id.fragmentContainerView)
        bottomNavigation = findViewById(R.id.homeNavigation)
        navigationRail = findViewById(R.id.homeNavigationRail)

        if (savedInstanceState == null) {
            PeriodicalNotificationScheduler(this,15)
            changeFragment(HomeFragment(), R.id.homeMenu.toString())
        }

        // Set the appropriate listener based on the active navigation view
        navigationRail?.setOnItemSelectedListener { menuItem ->
            navbarListener(menuItem)
        }
        bottomNavigation?.setOnItemSelectedListener { menuItem ->
            navbarListener(menuItem)
        }
    }

    override fun onResume() {
        super.onResume()
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        onBackPressedDispatcher.addCallback(this,callback)
    }

    private fun navbarListener(menuItem: MenuItem): Boolean {
        when (val id: Int = menuItem.itemId) {
            R.id.homeMenu -> changeFragment(HomeFragment(), id.toString())
            R.id.calendarMenu -> changeFragment(CalendarFragment(), id.toString())
            R.id.profileMenu -> changeFragment(ProfileFragment(), id.toString())
            R.id.addMenu -> changeFragment(GeofenceFragment(), id.toString())
            else -> false
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
        } else { supportFragmentManager.beginTransaction().run {
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