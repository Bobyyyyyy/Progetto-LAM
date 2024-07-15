package com.example.progettolam.ui.frontpageActivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.MainActivity
import com.example.progettolam.R
import com.example.progettolam.ui.homeFragment.HomeViewModel

class frontpageActivity: AppCompatActivity(){

    private lateinit var nameEdit: EditText
    private lateinit var confirmButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)
        nameEdit = findViewById(R.id.nameInput)
        confirmButton = findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener({
            val intent: Intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        })


    }


}