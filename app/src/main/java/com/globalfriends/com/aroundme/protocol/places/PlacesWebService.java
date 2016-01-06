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

    private void generateTextSearchQuery(Builder b) {
        if (!TextUtils.isEmpty(b.mLocation)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mLocation);
        }

        if (!TextUtils.isEmpty(b.mRadius)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mRadius);
        } else {
            mUrl.append(OPERATION_SEPARATOR).append("radius" + ASSIGNMENT + "50000"); // Max range
        }

        if (!TextUtils.isEmpty(b.mQuery)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mQuery);
        }

        if (!TextUtils.isEmpty(b.mLanguage)) {
            // It should be always from list o Supported languages.
            mUrl.append(OPERATION_SEPARATOR).append(b.mLanguage);
        }

        if (!TextUtils.isEmpty(b.mKey)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mKey);
        }
    }

    private PlacesWebService(Builder b) {
        mUrl.append(b.mUrl).append(b.mSearchType);
        if (!b.mSearchType.equalsIgnoreCase(PlaceRequestTypeEnum.SEARCH_TYPE_PHOTO.getSearchType())) {
            mUrl.append(QUERY_SEPARATOR).append(b.mResponseType);
        }

        mUrl.append(URL_DELIMITER);

        if (b.mSearchType.equals(PlaceRequestTypeEnum.SEARCH_TYPE_TEXT.getSearchType())) {
            generateTextSearchQuery(b);
            return;
        }

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

        if (!TextUtils.isEmpty(b.mPlaceId)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mPlaceId);
        }

        if (!TextUtils.isEmpty(b.mPhotoReference)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mPhotoReference);
        }

        if (!TextUtils.isEmpty(b.mPhotoMaxHeight)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mPhotoMaxHeight);
        }

        if (!TextUtils.isEmpty(b.mPhotoMaxWidth)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mPhotoMaxWidth);
        }

        if (!TextUtils.isEmpty(b.mInput)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mInput);
        }

        if (!TextUtils.isEmpty(b.mPageToken)) {
            mUrl.append(OPERATION_SEPARATOR).append(b.mPageToken);
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
     * Supportive Builder Class for making PlaceInfo Query
     */
    public static class Builder {
        //Set default URL an Query types
        private String mUrl = "https://maps.googleapis.com/maps/api/place/";
        private String mSearchType = PlaceRequestTypeEnum.SEARCH_TYPE_NEARBY.getSearchType();
        private String mResponseType = PlaceResponseEnum.RESP_JSON.getResponseType();
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
        private String mPlaceId;
        private String mPhotoReference;
        private String mPhotoMaxWidth;
        private String mPhotoMaxHeight;
        private String mInput; // Used for autocomplete
        private String mPageToken;

        public Builder setSearchType(final PlaceRequestTypeEnum mOperation) {
            this.mSearchType = mOperation.getSearchType();
            return this;
        }

        public Builder setResponseType(final PlaceResponseEnum mResponseType) {
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

        public Builder setRadius(final Double val) {
            this.mRadius = "radius" + ASSIGNMENT + Double.toString(val);
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

        public Builder setPageToken(final String pageToken) {
            this.mPageToken = "pagetoken" + ASSIGNMENT + pageToken;
            return this;
        }

        public Builder setName(final String val) {
            this.mName = "name" + ASSIGNMENT + val;
            return this;
        }

        public Builder setOpenNow(final boolean val) {
            this.mOpenNow = "opennow" + ASSIGNMENT + ((val) ? "true" : "false");
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

        public Builder setSensor(final boolean val) {
            this.mSensor = "sensor" + ASSIGNMENT + (val ? "true" : "false");
            return this;
        }

        public Builder setQuery(String val) {
            this.mQuery = "query" + ASSIGNMENT + val;
            return this;
        }

        public Builder setLanguage(final String val) {
            this.mLanguage = "language" + ASSIGNMENT + PlacesSupportedLanguages.getLanguage(val);
            return this;
        }

        public Builder setPlaceId(final String val) {
            this.mPlaceId = "placeid" + ASSIGNMENT + val;
            return this;
        }

        public Builder setPhotoReference(final String val) {
            this.mPhotoReference = "photoreference" + ASSIGNMENT + val;
            return this;
        }

        public Builder setPhotoWidth(final int val) {
            String value;
            if (val > 1600) {
                value = Integer.toString(1600);
            } else {
                value = Integer.toString(val);
            }
            this.mPhotoMaxWidth = "maxwidth" + ASSIGNMENT + value;
            return this;
        }

        public Builder setPhotoHeight(final int val) {
            String value;
            if (val > 1600) {
                value = Integer.toString(1600);
            } else {
                value = Integer.toString(val);
            }
            this.mPhotoMaxHeight = "maxheight" + ASSIGNMENT + value;
            return this;
        }

        public Builder setInput(final String input) {
            this.mInput = "input" + ASSIGNMENT + input;
            return this;
        }

        public PlacesWebService build() {
            return new PlacesWebService(this);
        }
    }
}
