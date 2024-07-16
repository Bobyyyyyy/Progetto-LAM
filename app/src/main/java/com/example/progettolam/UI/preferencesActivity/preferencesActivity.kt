package com.example.progettolam.UI.preferencesActivity

import android.os.Bundle

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.progettolam.DataStore.SharedPreferences
import com.example.progettolam.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class preferencesActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_activity)
        val preferenceDataStore = SharedPreferences(this)

        val textView: TextView = findViewById(R.id.nameTest)

        CoroutineScope(Dispatchers.IO).launch() {
            preferenceDataStore.getPreferences().collect{
                withContext(Dispatchers.Main) {
                    textView.text = it.username
                }
            }
        }
    }

}