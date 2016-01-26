package com.globalfriends.com.aroundme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
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
public class SettingsFragment extends PreferenceFragmentCompat {
    private ToolbarUpdateListener mToolbarUpdater;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.aroundme_prefrence);
        ListPreference listPref = (ListPreference) findPreference("language_pref");
        if (listPref.getValue() != null) {
            listPref.setSummary(listPref.getValue());
        } else {
            listPref.setSummary(PreferenceManager.getPreferredLanguage());
            listPref.setValue(PreferenceManager.getPreferredLanguage());
        }
        Set<String> languageList = PlacesSupportedLanguages.getListOfLanguages();
        CharSequence[] langArray = languageList.toArray(new CharSequence[languageList.size()]);
        listPref.setEntries(langArray);
        listPref.setEntryValues(langArray);
        listPref.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {
                preference.setSummary((String) o);
                PreferenceManager.setPreferredLanguage((String) o);
                return true;
            }
        });

        // Set sorting criteria
        ListPreference sortPref = (ListPreference) findPreference("sorting_criteria");
        if (sortPref.getValue() != null) {
            sortPref.setSummary(sortPref.getValue());
        } else {
            sortPref.setSummary(PreferenceManager.getPreferredSorting());
        }
        CharSequence[] sortingArray = {getString(R.string.sorting_distance), getString(R.string.sorting_rating)};
        sortPref.setEntries(sortingArray);
        sortPref.setEntryValues(sortingArray);
        sortPref.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {
                preference.setSummary((String) o);
                PreferenceManager.setPreferredSorting((String) o);
                return true;
            }
        });

        //Rated results only
        final CheckBoxPreference ratingPref = (CheckBoxPreference) findPreference("rating_preference");
        ratingPref.setChecked(PreferenceManager.getRatedOnlySelection());
        ratingPref.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object val) {
                if (val instanceof Boolean) {
                    Boolean boolVal = (Boolean)val;
                    PreferenceManager.setRatedOnlySelection(boolVal);
                }
                return true;
            }
        });

        // Miles and KM switch
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
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mToolbarUpdater = (ToolbarUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarUpdater.onNavigationEnabled(false);
        mToolbarUpdater.onSearchBarEnabled(false);
        mToolbarUpdater.settingsOptionUpdate(false);
        getActivity().setTitle(R.string.menu_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
