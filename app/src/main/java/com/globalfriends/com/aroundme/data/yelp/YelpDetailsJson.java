package com.globalfriends.com.aroundme.data.yelp;

import com.globalfriends.com.aroundme.data.DefaultPlaceDetails;

import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class YelpDetailsJson extends DefaultPlaceDetails {

    public YelpDetailsJson(final JSONObject response) {
        super(response);
    }
}
