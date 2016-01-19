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
    protected String mInternationalNumber;
    protected String mRating;
    protected String mPhoneNumber;
    protected String mWebUrl;
    protected String mName;
    protected String mNoOfReviews;
    protected Double mLatitude;
    protected Double mLongitude;
    protected boolean mPermanentlyClosed;
    protected boolean mOpenNow;
    protected String mIcon;
    protected String mRatingUrl;
    protected String mRatingColor;
    protected int mReviewCount;
    protected List<String> mWeeklyTimings = new ArrayList<String>();
    protected JSONObject mResponse;
    protected List<PlacePhotoMetadata> mPhotoList = new ArrayList<PlacePhotoMetadata>();
    protected List<PlaceReviewMetadata> mReviewList = new ArrayList<PlaceReviewMetadata>();

    @Override
    public boolean isPermanentlyClosed() {
        return mPermanentlyClosed;
    }

    @Override
    public List<String> getWeeklyTimings() {
        return mWeeklyTimings;
    }

    @Override
    public int getReviewCount() {
        return mReviewCount;
    }

    @Override
    public String getPlaceHolderIcon() {
        return mIcon;
    }

    @Override
    public String getRatingUrl() {
        return mRatingUrl;
    }

    public DefaultPlaceDetails(JSONObject response) {
        mResponse = response;
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    @Override
    public String getRating() {
        return mRating;
    }

    @Override
    public String getInternationalPhoneNumber() {
        return mInternationalNumber;
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
    public boolean isOpenNow() {
        return mOpenNow;
    }

    @Override
    public String toString() {
        return "DefaultPlaceDetails{" +
                "mDistance='" + mDistance + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mInternationalNumber='" + mInternationalNumber + '\'' +
                ", mWebUrl='" + mWebUrl + '\'' +
                ", mName='" + mName + '\'' +
                ", mNoOfReviews='" + mNoOfReviews + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mPermanentlyClosed=" + mPermanentlyClosed +
                ", mOpenNow=" + mOpenNow +
                ", mWeeklyTimings=" + mWeeklyTimings +
                ", mResponse=" + mResponse +
                ", mPhotoList=" + mPhotoList +
                ", mReviewList=" + mReviewList +
                '}';
    }

    @Override
    public void updatePhotoToList(PlacePhotoMetadata photo) {
        mPhotoList.add(photo);
    }

    @Override
    public List<PlacePhotoMetadata> getPhotos() {
        return mPhotoList;
    }

    @Override
    public void updateReviewToList(PlaceReviewMetadata review) {
        mReviewList.add(review);
    }

    @Override
    public List<PlaceReviewMetadata> getReviewList() {
        return mReviewList;
    }

    @Override
    public String getRatingColor() {
        return mRatingColor;
    }
}
