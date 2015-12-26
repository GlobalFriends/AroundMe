package com.globalfriends.com.aroundme.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.DistanceFormatEnum;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.protocol.places.PlacesSupportedLanguages;

import java.util.Set;

/**
 * Created by swapna on 12/7/2015.
 */
public class SettingsFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.aroundme_prefrence);
        ListPreference listPref = (ListPreference) findPreference("language_pref");
        if (listPref.getValue() != null) {
            listPref.setSummary(listPref.getValue());
        }else {
            listPref.setSummary(PreferenceManager.getPreferedLanguage());
        }
        Set<String> languageList = PlacesSupportedLanguages.getListOfLanguages();
        CharSequence[] langArray = languageList.toArray(new CharSequence[languageList.size()]);
        listPref.setEntries(langArray);
        listPref.setEntryValues(langArray);
        listPref.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {
                preference.setSummary((String) o);
                PreferenceManager.setPreferedLanguage((String)o);
                return true;
            }
        });
        EditTextPreference editPref = (EditTextPreference) findPreference("distance_pref");
        editPref.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {
                preference.setSummary((String) o);
                PreferenceManager.setRadius(Integer.parseInt((String)o));
                return true;
            }
        });
        if (editPref.getText() != null) {
            editPref.setSummary(editPref.getText());
        }else{
            editPref.setSummary(""+PreferenceManager.getRadius());
        }
        SwitchPreferenceCompat milesPref = (SwitchPreferenceCompat) findPreference("miles_or_km_pref");
        milesPref.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {
                boolean val = (boolean) o;
                if (val) {
                    PreferenceManager.setDistanceFormat(DistanceFormatEnum.KILOMETER.getValue());
                    preference.setSummary("KM");
                } else {
                    PreferenceManager.setDistanceFormat(DistanceFormatEnum.MILES.getValue());
                    preference.setSummary("MILES");
                }
                return true;
            }
        });
        if (milesPref.isChecked())
            milesPref.setSummary("KM");
        else
            milesPref.setSummary("MILES");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
