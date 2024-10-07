package com.example.progettolam.UI.preferencesActivity

import android.os.Bundle
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.example.progettolam.R

class PreferencesFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.user_preferences, rootKey)

        findPreference<EditTextPreference>(getString(R.string.preferences_username))
            ?.setOnPreferenceChangeListener { _, newValue ->
                newValue.toString().isNotBlank()
            }


        findPreference<SwitchPreference>(getString(R.string.preferences_theme))
            ?.setOnPreferenceChangeListener { _, _ ->
                requireActivity().recreate();
                true
            }


    }



}

