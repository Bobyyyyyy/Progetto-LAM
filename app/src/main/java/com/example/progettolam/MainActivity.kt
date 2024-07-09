package com.example.progettolam

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.progettolam.ui.homeFragment.HomeFragment
import com.example.progettolam.ui.profileFragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


// da cambiare la gestione del backstack, vorrei farlo come instagram che quandi clicchi su una sezione ed è già nel backstack riapre quella.


class MainActivity : AppCompatActivity() {
    private lateinit var navigationBar: BottomNavigationView
    private lateinit var fragmentContainer: FragmentContainerView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentContainer = findViewById(R.id.fragmentContainerView)
        navigationBar = findViewById(R.id.homeNavigation)


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFragmentManager.popBackStackImmediate()
                navigationBar.selectedItemId = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)?.tag?.toInt()
                    ?: R.id.homeMenu

            }
        }

        onBackPressedDispatcher.addCallback(this,callback)
        navigationBar.setOnItemSelectedListener { menuItem ->
            navbarListener(menuItem)
        }

    }


    private fun changeFragment(fragment: Fragment, id: String) {
        supportFragmentManager.beginTransaction().run {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView, fragment, id)
            addToBackStack(null)
            commit()
        }
    }

    private fun navbarListener(menuItem: MenuItem): Boolean {
        when (val id: Int? = menuItem.itemId) {
            R.id.homeMenu -> {
                if(!(menuItem.isChecked)) {
                    changeFragment(HomeFragment(),id.toString())
                }
                true
            }
            R.id.calendarMenu -> {
                true
            }
            R.id.profileMenu -> {
                if(!(menuItem.isChecked)) {
                    changeFragment(ProfileFragment(),id.toString())
                }
                true
            }
            R.id.addMenu -> {
                true
            }
            else -> { false }
        }
        return true
    }
}