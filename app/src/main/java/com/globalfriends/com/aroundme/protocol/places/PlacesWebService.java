package com.globalfriends.com.aroundme.protocol.places;

import android.text.TextUtils;

/**
 * Created by vishal on 11/8/2015.
 */
public class PlacesWebService {
    private final static String OPERATION_SEPARATOR = "&";
    private final static String QUERY_SEPARATOR = "/";
    private final static String TYPES_SEPARATOR = "|";
    private final static String URL_DELIMITER = "?";
    private final static String ASSIGNMENT = "=";
    //Url
    private StringBuilder mUrl = new StringBuilder();

    private PlacesWebService(Builder b) {
        mUrl.append(b.mUrl).append(b.mSearchType).append(QUERY_SEPARATOR).
                append(b.mResponseType).append(URL_DELIMITER);

        if (!TextUtils.isEmpty(b.mLocation)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mLocation);
        }

        if (!TextUtils.isEmpty(b.mRadius)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mRadius);
        }

        if (!TextUtils.isEmpty(b.mLanguage)) {
            // It should be always from list o Supported languages.
            mUrl.append(OPERATION_SEPARATOR).append(b.mLanguage);
        }

        if (!TextUtils.isEmpty(b.mMinPrice)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mMinPrice);
        }

        if (!TextUtils.isEmpty(b.mMaxPrice)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mMaxPrice);
        }

        if (!TextUtils.isEmpty(b.mOpenNow)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mOpenNow);
        }

        if (!TextUtils.isEmpty(b.mTypes)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mTypes.replace(",", TYPES_SEPARATOR));
        }

        if (!TextUtils.isEmpty(b.mSensor)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mSensor);
        }

        if (!TextUtils.isEmpty(b.mZagatSelected)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mZagatSelected);
        }

        if (!TextUtils.isEmpty(b.mRankBy)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mRankBy);
        }

        if (!TextUtils.isEmpty(b.mQuery)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mQuery);
        }

        //Last one has to be API Key...
        if (!TextUtils.isEmpty(b.mKey)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mKey);
        }
    }

    public String getUrl() {
        return mUrl.toString();
    }

    /**
     * Supportive Builder Class for making Places Query
     */
    public static class Builder {
        //Url and query type
        private String mUrl = "https://maps.googleapis.com/maps/api/place/";
        private String mSearchType;
        private String mResponseType;
        //Params
        private String mQuery;
        private String mKey;
        private String mLocation;
        private String mRadius;
        private String mLanguage;
        private String mRankBy;
        private String mKeyword; //Radar Search
        private String mName; //Radar Search
        private String mOpenNow;
        private String mTypes;
        private String mZagatSelected;
        private String mMinPrice;
        private String mMaxPrice;
        private String mSensor;

        public Builder setSearchType(final PlaceSearchTypeEnum mOperation) {
            this.mSearchType = mOperation.getSearchType();
            return this;
        }

        public Builder setResponseType(final PlacesResponseEnum mResponseType) {
            this.mResponseType = mResponseType.getResponseType();
            return this;
        }

        public Builder setKey(final String val) {
            this.mKey = "key" + ASSIGNMENT + val;
            return this;
        }

        public Builder setLocation(final String val) {
            this.mLocation = "location" + ASSIGNMENT + val;
            return this;
        }

        public Builder setRadius(final String val) {
            this.mRadius = "radius" + ASSIGNMENT + val;
            return this;
        }

        public Builder setRankBy(final PlacesRankByEnum val) {
            this.mRankBy = "rankby" + ASSIGNMENT + val.getDescription();
            return this;
        }

        public Builder setKeyword(final String val) {
            this.mKeyword = "keyword" + ASSIGNMENT + val;
            return this;
        }

        public Builder setName(final String mName) {
            this.mName = "name" + ASSIGNMENT + mName;
            return this;
        }

        public Builder setOpenNow(final boolean val) {
            this.mOpenNow = "opennow" + ASSIGNMENT + ((val) ? "true" : "false");
            return this;
        }

        public Builder setRankBy(String mRankBy) {
            this.mRankBy = "rankby" + ASSIGNMENT + mRankBy;
            return this;
        }

        public Builder setTypes(final String val) {
            this.mTypes = "types" + ASSIGNMENT + val;
            return this;
        }

        public Builder setZagatSelected(final boolean val) {
            this.mZagatSelected = "zagatselected" + ASSIGNMENT + ((val) ? "true" : "false");
            return this;
        }

        public Builder setMinPrice(final PriceRangeEnum val) {
            this.mMinPrice = "minprice" + ASSIGNMENT + Integer.toString(val.getPriceRange());
            return this;
        }

        public Builder setMaxPrice(final PriceRangeEnum val) {
            this.mMaxPrice = "maxprice" + ASSIGNMENT + Integer.toString(val.getPriceRange());
            return this;
        }

        public Builder setSensor(final boolean mSensor) {
            this.mSensor = "sensor=" + ASSIGNMENT + (mSensor ? "true" : "false");
            return this;
        }

        public Builder setQuery(String mQuery) {
            this.mQuery = "query" + ASSIGNMENT + mQuery;
            return this;
        }

        public Builder setLanguage(final String mLanguage) {
            this.mLanguage = "language" + ASSIGNMENT + PlacesSupportedLanguages.getLanguage(mLanguage);
            return this;
        }

        public PlacesWebService build() {
            return new PlacesWebService(this);
        }
    }
}
