package com.globalfriends.com.aroundme.data;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishal on 11/29/2015.
 */
public class DefaultPlaceDetails implements IPlaceDetails {
    protected String mDistance;
    protected String mAddress;
    protected String mPhoneNumber;
    protected String mWebUrl;
    protected String mName;
    protected String mNoOfReviews;
    protected Double mLatitude;
    protected Double mLongitude;
    protected boolean mOpneNow;
    protected List<String> mWeeklyTimings = new ArrayList<String>();
    protected JSONObject mResponse;

    public DefaultPlaceDetails(JSONObject response) {
        mResponse = response;
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

    @Override
    public Double getLatitude() {
        return mLatitude;
    }

    @Override
    public Double getLongitude() {
        return mLongitude;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("mDistance=" + mDistance).append(" mAddress" + mAddress)
                .append(" mPhoneNumber" + mPhoneNumber).append(" mWebUrl" + mWebUrl)
                .append(" mName" + mName).append(" mNoOfReviews=" + mNoOfReviews)
                .append(" mLatitude=" + mLatitude).append(" mLongitude=" + mLongitude)
                .append(" mOpneNow=" + mOpneNow);
        return builder.toString();
    }
}
