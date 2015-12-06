package com.globalfriends.com.aroundme.data.places;

import android.widget.TextView;

import com.globalfriends.com.aroundme.data.DefaultPlaceDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by vishal on 11/19/2015.
 */
public class GooglePlaceDetailsJson extends DefaultPlaceDetails {
    public GooglePlaceDetailsJson(JSONObject response) {
        super(response);
        JsonTOPointToReference(mResponse);
    }

    private GooglePlaceDetailsJson JsonTOPointToReference(JSONObject response) {
        try {
            JSONObject geometry = (JSONObject) response.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            mLatitude = ((Double) location.get("lat"));
            mLongitude = ((Double) location.get("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mAddress = response.getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mPhoneNumber = response.getString("international_phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mName = response.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            mWebUrl = response.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject openingHours = null;
        try {
            openingHours = (JSONObject) response.get("opening_hours");
            mOpneNow = openingHours.getBoolean("open_now");
            JSONArray weeklyText = openingHours.getJSONArray("weekday_text");
            if (weeklyText != null) {
                for (int i = 0; i < weeklyText.length(); i++) {
                    mWeeklyTimings.add(weeklyText.get(i).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
}
