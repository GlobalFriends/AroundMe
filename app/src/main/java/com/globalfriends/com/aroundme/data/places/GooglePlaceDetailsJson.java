package com.globalfriends.com.aroundme.data.places;

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

            mAddress = response.getString("formatted_address");
            mPhoneNumber = response.getString("international_phone_number");
            mName = response.getString("name");
            mWebUrl = response.getString("url");

            JSONObject openingHours = (JSONObject) response.get("opening_hours");
            mOpneNow = openingHours.getBoolean("open_now");
            String weeklyText = openingHours.getString("weekday_text");
            mWeeklyTimings.addAll(Arrays.asList(weeklyText.split(",")));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
}
