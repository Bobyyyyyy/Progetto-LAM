package com.example.progettolam.UI.preferencesActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.progettolam.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class preferencesActivity: AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var editUsername: EditText

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shared_preferences)
        fun String.toEditable(): Editable =
            Editable.Factory.getInstance().newEditable(this)

        initViews()

        val value = resources.getString(R.string.preferences_username)
        val defaultShared = PreferenceManager.getDefaultSharedPreferences(this).all


        val sharedPref: SharedPreferences = this.getSharedPreferences(getString(R.string.preferences_user), Context.MODE_PRIVATE)

        val name = sharedPref.getString(getString(R.string.preferences_username), value)


        if (name != null) {
            if (defaultShared != null) {
                editUsername.text = name.toEditable()
            }
        }


        saveButton.setOnClickListener {
            if (editUsername.text.toString().isNotBlank()) {
                with(sharedPref.edit()) {
                    putString(
                        getString(com.example.progettolam.R.string.preferences_username),
                        editUsername.text.toString()
                    )
                    apply()
                }

            }
        }
    }

    private fun initViews() {
        saveButton = findViewById(R.id.saveButton)
        editUsername = findViewById(R.id.username)
    }

}