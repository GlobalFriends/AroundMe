package com.globalfriends.com.aroundme.data;

import org.json.JSONObject;

/**
 * Created by vishal on 11/29/2015.
 */
public class DefaultPlaceDetails implements IPlaceDetails {
    public String mDistance;
    private String mAddress;
    private String mPhoneNumber;
    private String mWebUrl;
    private String mName;
    private String mNoOfReviews;

    public DefaultPlaceDetails(JSONObject response) {
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    @Override
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Override
    public String getWebUrl() {
        return mWebUrl;
    }

    @Override
    public String getDistance() {
        return mDistance;
    }

    @Override
    public String getPlaceName() {
        return mName;
    }
}
