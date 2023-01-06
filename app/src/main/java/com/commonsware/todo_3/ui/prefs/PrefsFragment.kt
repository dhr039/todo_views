package com.commonsware.todo_3.ui.prefs

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.commonsware.todo_3.R

class PrefsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(state: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }
}
