package com.addie.maxfocus.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.addie.maxfocus.R;

import java.util.Objects;

import timber.log.Timber;


/**
 * Fragment which contains preferences regarding various settings of app
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String CALLING_CLASS_KEY = "calling_class";

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.pref_main);

        PreferenceScreen prefScreen = getPreferenceScreen();
        sharedPreferences = prefScreen.getSharedPreferences();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);

            if(p instanceof ListPreference){

                String value = sharedPreferences.getString(p.getKey(), "");
                Timber.d(value);
                setPreferenceSummary(p, value);
            }
            if (Objects.equals(p.getKey(), getString(R.string.pref_app_time_key))){
                String value = sharedPreferences.getString(getString(R.string.pref_app_time_key),"");
                setPreferenceSummary(p,value);
            }
        }
    }

    /**
     * Updates the summary for the preference
     *
     * @param preference The preference to be updated
     * @param value      The value that the preference was updated to
     */
    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            // For list preferences, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        if (preference.getKey().equals(getString(R.string.pref_app_time_key))){

            preference.setSummary(value+" minutes");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {

            if(key.equals(getString(R.string.pref_app_time_key))) {
                String value = sharedPreferences.getString(getString(R.string.pref_app_time_key), "");
                setPreferenceSummary(preference, value);
            }
        }
    }


    //TODO Implement
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
