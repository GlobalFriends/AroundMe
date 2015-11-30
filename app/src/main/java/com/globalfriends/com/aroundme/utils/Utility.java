package com.globalfriends.com.aroundme.utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;

import com.globalfriends.com.aroundme.AroundMeApplication;
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

            File gpxfile = new File(root, sFileName);
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
}
