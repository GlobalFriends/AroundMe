package com.globalfriends.com.aroundme.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.DistanceFormatEnum;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.logging.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by vishal on 11/22/2015.
 */
public class Utility {
    private static final String TAG = " Utility";

    /**
     * @param zipCode
     * @return
     */
    public static String getLocationFromZip(final String zipCode) {
        //TODO: We might have to use GeoCoding from Places Maps API
        return null;
    }

    public static void generateNoteOnSD(String sFileName, String sBody) {
        try {
            Logger.i(TAG, "Response=" + sBody);
            File root = new File(Environment.getExternalStorageDirectory(), "AroundMe");
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, sFileName + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns your Zip code based on latitude and longitude
     *
     * @return
     */
    public static String getZipCode() {
        try {
            Geocoder gcd = new Geocoder(AroundMeApplication.getContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(Double.valueOf(PreferenceManager.getLatitude()),
                    Double.valueOf(PreferenceManager.getLongitude()), 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getPostalCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns your country code based on latitude and longitude
     *
     * @return
     */
    public static String getCountryCodeFromLocation() {
        try {
            Geocoder gcd = new Geocoder(AroundMeApplication.getContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(Double.valueOf(PreferenceManager.getLatitude()),
                    Double.valueOf(PreferenceManager.getLongitude()), 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CFInds distance between 2 places based on latitude and longitude
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static String distanceFromLatitudeLongitude(double lat1, double lon1, double lat2,
                                                       double lon2, final DistanceFormatEnum type) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        switch (type) {
            case MILES:
                dist = (dist * 60 * 1.1515);
                return (String.format("%.2f ", dist) + AroundMeApplication.getContext().
                        getResources().getString(R.string.miles_notation));
            case KILOMETER:
                dist = (dist * 1.609344);
                return (String.format("%.2f ", dist) + AroundMeApplication.getContext().
                        getResources().getString(R.string.kilometer_notation));
            default:
        }

        return String.format("%.2f", dist);
    }

    public static double distanceInMeters(final DistanceFormatEnum type, final int unit) {
        double meters = 0;
        switch (type) {
            case MILES:
                meters = (unit * 1609.34);
                break;
            case KILOMETER:
                meters = (meters * 1000);
                break;
            default:
        }
        return meters;
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
