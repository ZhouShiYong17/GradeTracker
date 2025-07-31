package sg.com.mapp.gradetracker

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import sg.com.mapp.gradetracker.util.Registry

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()

    }

    class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            setupPref()
        }

        private fun setupPref() {
            val currentSemesterPref = findPreference(KEY_SET_CURRENT_SEMESTER) as ListPreference
            val semesters = Registry.semesterRepo.semesters

            currentSemesterPref.entries = semesters.map {
                activity.getString(R.string.format_semester, it.year, it.semester)
            }.toTypedArray()

            currentSemesterPref.entryValues = semesters.map {
                it.semesterNo.toString()
            }.toTypedArray()
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            if (key == KEY_SET_CURRENT_SEMESTER) {
                val currentSemesterPref = findPreference(key)

                if (sharedPreferences != null) {
                    currentSemesterPref.summary = sharedPreferences.getString(key, "")
                }
            }
        }

    }

    companion object {
        @JvmStatic
        val KEY_SET_CURRENT_SEMESTER = "pref_set_current_semester"
        @JvmStatic
        val KEY_IMPORT_TO_GOOGLE_DRIVE = "pref_import_drive"
        @JvmStatic
        val KEY_EXPORT_TO_GOOGLE_DRIVE = "pref_export_drive"
    }

}
