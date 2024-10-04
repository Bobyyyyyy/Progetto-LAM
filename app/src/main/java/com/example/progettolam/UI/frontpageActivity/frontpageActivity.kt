package com.example.progettolam.UI.frontpageActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.progettolam.MainActivity
import com.example.progettolam.R

class frontpageActivity: AppCompatActivity(){

    private lateinit var nameEdit: EditText
    private lateinit var confirmButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)
        initViews()
        //IDEA: Creare una activity vuota e da quella si decide se mandarlo nella activity principale o in quella di selezione del nome

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        val value = resources.getString(R.string.preferences_username)
        val storedName = sharedPref?.getString(getString(R.string.preferences_username), value)

        val storedTheme = sharedPref?.getBoolean(getString(R.string.preferences_theme),false)
        if (storedTheme == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (storedName.toString().isNotBlank()) {
            val intent: Intent = Intent(this@frontpageActivity, MainActivity::class.java)
            startActivity(intent)
        }


        confirmButton.setOnClickListener {
            val username = nameEdit.text.toString()
            if(username.isNotBlank()) {
                if (sharedPref != null) {
                    with (sharedPref.edit()) {
                        putString(getString(R.string.preferences_username),username)
                        apply()
                    }
                }
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }



    }

    private fun initViews() {
        nameEdit = findViewById(R.id.nameInput)
        confirmButton = findViewById(R.id.confirmButton)

    }


}

