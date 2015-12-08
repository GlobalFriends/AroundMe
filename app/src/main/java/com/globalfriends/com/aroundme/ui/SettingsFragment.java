package com.globalfriends.com.aroundme.ui;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.protocol.places.PlacesSupportedLanguages;

import java.util.Set;

/**
 * Created by swapna on 12/7/2015.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.aroundme_prefrence);
        ListPreference listPref = (ListPreference) findPreference("language_pref");
        Set<String> languageList = PlacesSupportedLanguages.getListOfLanguages();
        CharSequence[] langArray = languageList.toArray(new CharSequence[languageList.size()]);
        listPref.setEntries(langArray);
        listPref.setEntryValues(langArray);
        EditTextPreference editPref = (EditTextPreference)findPreference("distance_pref");
        editPref.setDefaultValue(0);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equalsIgnoreCase("miles_or_km_pref")) {
            boolean val = (boolean) newValue;
            if (val) {
                preference.setSummary("miles");
            }else {
                preference.setSummary("km");
            }
        } else if (preference.getKey().equalsIgnoreCase("distance_pref")) {
            preference.setSummary((String)newValue);
        } else if(preference.getKey().equalsIgnoreCase("language_pref")){
            preference.setSummary((String)newValue);
        }
        return true;
    }
}
