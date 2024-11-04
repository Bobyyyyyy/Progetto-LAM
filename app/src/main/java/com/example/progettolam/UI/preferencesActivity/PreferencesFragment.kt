package com.example.progettolam.UI.preferencesActivity

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.progettolam.R

class PreferencesFragment: PreferenceFragmentCompat() {

    companion object {
        const val MIN_HEIGHT = 100
        const val MAX_HEIGHT = 250
        const val MIN_WEIGHT = 20
        const val MAX_WEIGHT = 300
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
                Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.name_update), Toast.LENGTH_SHORT).show();
                newValue.toString().isNotBlank()
            }

        findPreference<SwitchPreference>(getString(R.string.preferences_theme))
            ?.setOnPreferenceChangeListener { _, _ ->
                requireActivity().recreate();
                true
            }

        findPreference<EditTextPreference>(getString(R.string.preferences_height))
            ?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().toIntOrNull() in MIN_HEIGHT..MAX_HEIGHT) {
                    val newValueStr = newValue.toString()
                    Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.success_add_height), Toast.LENGTH_SHORT).show();
                    newValueStr.isNotEmpty() && newValueStr.matches("\\d+".toRegex())
                } else {
                    Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_add_height), Toast.LENGTH_SHORT).show();
                    false
                }

            }


        findPreference<EditTextPreference>(getString(R.string.preferences_weight))
            ?.setOnPreferenceChangeListener { _, newValue ->
                if(newValue.toString().toIntOrNull() in MIN_WEIGHT..MAX_WEIGHT) {
                    val newValueStr = newValue.toString()
                    Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.success_add_weight), Toast.LENGTH_SHORT).show();
                    newValueStr.isNotEmpty() && newValueStr.matches("\\d+".toRegex())
                } else {
                    Toast.makeText(requireContext(), ContextCompat.getString(requireContext(), R.string.error_add_weight), Toast.LENGTH_SHORT).show();
                    false
                }
            }




    }



}

