package com.example.progettolam.UI.preferencesActivity

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.example.progettolam.R

class PreferencesFragment: PreferenceFragmentCompat() {

    companion object {
        const val minHeight = 100
        const val maxHeight = 250
        const val minWeight = 20
        const val maxWeight = 300
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.user_preferences, rootKey)


        val heightPreference: EditTextPreference? = findPreference(getString(R.string.preferences_height))

        val weightPreference: EditTextPreference? = findPreference(getString(R.string.preferences_height))


        heightPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        weightPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        findPreference<EditTextPreference>(getString(R.string.preferences_username))
            ?.setOnPreferenceChangeListener { _, newValue ->
                newValue.toString().isNotBlank()
            }

        findPreference<SwitchPreference>(getString(R.string.preferences_theme))
            ?.setOnPreferenceChangeListener { _, _ ->
                requireActivity().recreate();
                true
            }

        findPreference<EditTextPreference>(getString(R.string.preferences_height))
            ?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toIntOrNull() in minHeight..maxHeight) {
                    val newValueStr = newValue.toString()
                    newValueStr.isNotEmpty() && newValueStr.matches("\\d+".toRegex())
                }
                else false

            }


        findPreference<EditTextPreference>(getString(R.string.preferences_weight))
            ?.setOnPreferenceChangeListener { _, newValue ->
                if(newValue.toString().toIntOrNull() in minWeight..maxWeight) {
                    val newValueStr = newValue.toString()
                    newValueStr.isNotEmpty() && newValueStr.matches("\\d+".toRegex())
                }
                else false
            }




    }



}

