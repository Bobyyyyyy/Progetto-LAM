package com.example.progettolam.DataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
val Context.dataStore : DataStore<Preferences> by preferencesDataStore("user_info")

class SharedPreferences(context: Context) {
    val pref = context.dataStore
    companion object {
        var username = stringPreferencesKey("USERNAME")
    }

    suspend fun changePreferences(userinfo: userPreferences) {
        pref.edit {
            it[username] = userinfo.username
        }
    }

    suspend fun getPreferences() = pref.data.map {
        userPreferences(
            username = it[username]?:""
        )
    }



}