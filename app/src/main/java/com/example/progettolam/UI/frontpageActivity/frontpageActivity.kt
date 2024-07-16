package com.example.progettolam.UI.frontpageActivity

import android.content.Intent
import android.os.Bundle

import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.progettolam.DataStore.SharedPreferences
import com.example.progettolam.DataStore.userPreferences
import com.example.progettolam.MainActivity
import com.example.progettolam.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class frontpageActivity: AppCompatActivity(){

    private lateinit var nameEdit: EditText
    private lateinit var confirmButton: ImageView

    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)
        nameEdit = findViewById(R.id.nameInput)
        confirmButton = findViewById(R.id.confirmButton)
        val preferenceDataStore = SharedPreferences(this)
        confirmButton.setOnClickListener {
            scope.launch() {
                preferenceDataStore.changePreferences(userPreferences(nameEdit.text.toString()))
            }
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}