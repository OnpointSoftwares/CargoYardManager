package com.example.cargoyard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefferences, rootKey) // Load preferences from XML

        val signOutPref = findPreference<Preference>("sign_out")
        signOutPref?.setOnPreferenceClickListener {
            // Clear user session and navigate to login screen
            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            preferences.edit().clear().apply() // Clear shared preferences

            Toast.makeText(context, "Signing out...", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            // Navigate to LoginActivity
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
            startActivity(intent)

            true
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "theme" -> {
                val themePref = findPreference<ListPreference>(key)
                val selectedTheme = themePref?.value
                Toast.makeText(context, "Theme changed to $selectedTheme", Toast.LENGTH_SHORT).show()
                applyTheme(selectedTheme)
            }
            "sync_frequency" -> {
                val syncPref = findPreference<ListPreference>(key)
                val selectedFrequency = syncPref?.value
                Toast.makeText(context, "Sync frequency set to $selectedFrequency", Toast.LENGTH_SHORT).show()
                // TODO: Handle sync frequency logic if applicable
            }
        }
    }

    private fun applyTheme(theme: String?) {
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            // You can add more theme options if you have them
        }
        // Optionally restart the activity to apply the new theme
        activity?.recreate()
    }
}
