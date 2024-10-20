package com.example.progettolam.UI.profileFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {


    private val _username = MutableLiveData<String>().apply {
        value = ""
    }

    val username: LiveData<String> = _username

    fun changeUsername(value: String) {
        _username.value = value
    }
}