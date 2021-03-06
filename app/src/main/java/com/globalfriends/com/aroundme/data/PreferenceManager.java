package com.globalfriends.com.aroundme.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.utils.Utility;

import java.util.Map;

/**
 * Created by vishal on 11/22/2015.
 */
public class PreferenceManager {
    public static final String PREF_RADIUS = "radius"; //int
    public static final String PREF_DISTANCE_FORMAT = "format"; //int
    public static final String PREF_DISTANCE_VALUE = "distance_value"; //int
    public static final String PREF_PREFERED_LANGUAGE = "language_value"; //String
    public static final String PREF_LATITUDE = "latitude"; //String
    public static final String PREF_LONGITUDE = "longitude"; //String
    public static final String PREF_SORTING = "sorting"; //String
    public static final String PREF_RATING = "rating"; //String
    private static final String FILE_NAME = "pref_file";
    private static SharedPreferences mPreference = AroundMeApplication.getContext().
            getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

    public static SharedPreferences getPreference() {
        return mPreference;
    }

    public static double getRadius() {
        // check distance format and verify if its a miles or kilometers..
        return Utility.distanceInMeters(getDistanceFormat(), mPreference.getInt(PREF_DISTANCE_VALUE, 5));
    }

    public static void setRadius(int value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(PREF_RADIUS, value);
        editor.commit();
    }

    public static DistanceFormatEnum getDistanceFormat() {
        return DistanceFormatEnum.getFormat(mPreference.getInt
                (PREF_DISTANCE_FORMAT, DistanceFormatEnum.MILES.getValue()));
    }

    public static void setDistanceFormat(int value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(PREF_DISTANCE_FORMAT, value);
        editor.commit();
    }

    public static String getPreferredLanguage() {
        return mPreference.getString(PREF_PREFERED_LANGUAGE, "English");
    }

    public static void setPreferredLanguage(String value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(PREF_PREFERED_LANGUAGE, value);
        editor.commit();
    }

    public static String getPreferredSorting() {
        return mPreference.getString(PREF_SORTING,
                AroundMeApplication.getContext().getString(R.string.sorting_distance));
    }

    public static void setPreferredSorting(String value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(PREF_SORTING, value);
        editor.commit();
    }

    public static Boolean getRatedOnlySelection() {
        return mPreference.getBoolean(PREF_RATING, false);
    }

    public static void setRatedOnlySelection(Boolean value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putBoolean(PREF_RATING, value);
        editor.commit();
    }

    public static String getLocation() {
        if (getLatitude() == null ||
                getLongitude() == null) {
            return null;
        }
        return (mPreference.getString(PREF_LATITUDE, null) + "," + mPreference.getString(PREF_LONGITUDE, null));
    }

    public static String getLatitude() {
        return mPreference.getString(PREF_LATITUDE, null);
    }

    public static String getLongitude() {
        return mPreference.getString(PREF_LONGITUDE, null);
    }

    public static void putLocation(final String latitude, final String longitude) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(PREF_LATITUDE, latitude);
        editor.putString(PREF_LONGITUDE, longitude);
        editor.commit();
    }


    public static void dump() {
        Map<String, ?> all = mPreference.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            Logger.i("Preference:", "" + entry.getKey() + "/" + entry.getValue());
        }
    }
}
