package sdccd.edu.laitinena7.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import sdccd.edu.laitinena7.R;

/**
 * Created by Tuulikki Laitinen on 5/18/2017.
 */

public class SettingsActivityFragment extends PreferenceFragment {
    // creates preferences GUI from preferences.xml file in res/xml
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); // load from XML

    }


}