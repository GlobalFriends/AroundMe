package com.globalfriends.com.aroundme.data.places;

import com.globalfriends.com.aroundme.data.DefaultPlaceDetails;

import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class GooglePlaceDetailsJson extends DefaultPlaceDetails {

    public GooglePlaceDetailsJson(JSONObject response) {
        super(response);
    }
}
