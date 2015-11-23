package com.globalfriends.com.aroundme.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.globalfriends.com.aroundme.AroundMeApplication;

/**
 * Created by vishal on 11/22/2015.
 */
public class PreferenceManager {
    public static final String PREF_RADIUS = "radius"; //int
    public static final String PREF_DISTANCE_FORMAT = "format"; //int
    public static final String PREF_DISTANCE_VALUE = "distance_value"; //int
    public static final String PREF_LATITUDE = "latitude"; //String
    public static final String PREF_LONGITUDE = "longitude"; //String
    private static final String FILE_NAME = "pref_file";
    private static SharedPreferences mPreference = AroundMeApplication.getContext().
            getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

    public static SharedPreferences getPreference() {
        return mPreference;
    }

    public static double getRadius() {
        // check distance format and verify if its a miles or kilometers..
        DistanceFormatEnum format = DistanceFormatEnum.getFormat(mPreference.getInt
                (PREF_DISTANCE_FORMAT, DistanceFormatEnum.MILES.getValue()));
        return DistanceFormatEnum.getDistanceInMeters(format, mPreference.getInt(PREF_DISTANCE_VALUE, 2000));
    }

    public static String getLocation() {
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
}
