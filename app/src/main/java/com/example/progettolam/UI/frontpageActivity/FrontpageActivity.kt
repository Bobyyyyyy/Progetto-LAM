package com.example.progettolam.UI.frontpageActivity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.progettolam.MainActivity
import com.example.progettolam.R

class FrontpageActivity: AppCompatActivity(){
    private lateinit var nameEdit: EditText
    private lateinit var confirmButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)
        initViews()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        val value = resources.getString(R.string.preferences_username_default)
        val storedName = sharedPref?.getString(getString(R.string.preferences_username), value)

        val storedTheme = sharedPref?.getBoolean(getString(R.string.preferences_theme),false)
        if (storedTheme == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (storedName.toString().isNotBlank() && storedName.toString() != value) {
            startActivity(Intent(this@FrontpageActivity, MainActivity::class.java))
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
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun initViews() {
        nameEdit = findViewById(R.id.nameInput)
        confirmButton = findViewById(R.id.confirmButton)
    }
}

