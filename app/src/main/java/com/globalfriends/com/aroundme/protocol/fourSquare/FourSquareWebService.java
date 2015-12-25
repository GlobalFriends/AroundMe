package com.globalfriends.com.aroundme.protocol.fourSquare;

import android.text.TextUtils;

/**
 * Created by Vishal on 12/20/2015.
 * Either supports a venue search query or place search query
 */
public class FourSquareWebService {
    private StringBuilder mUrl = new StringBuilder();
    private String querySeparator = "?";
    private String extraSeparator = "&";
    private String equalsTo = "=";

    private FourSquareWebService(Builder b) {
        mUrl.append(b.mUrl);
        if (b.mSearch) {
            mUrl.append("search").append(querySeparator);
            mUrl.append("ll").append(equalsTo).append(b.mLocation).append(extraSeparator);
        } else {
            mUrl.append(b.mPlaceId).append(querySeparator);
        }
        mUrl.append("client_id").append(equalsTo).append(b.mClientId).
                append(extraSeparator).append("client_secret").append(equalsTo).append(b.mClientKey).
                append(extraSeparator).append("v").append(equalsTo).append(b.mVersion);
    }

    public String getUrl() {
        return mUrl.toString();
    }

    /**
     * Supportive Builder Class for making PlaceInfo Query
     */
    public static class Builder {
        private String mVersion;
        private String mClientId;
        private String mClientKey;
        private String mLocation;
        private boolean mSearch;
        private String mPlaceId;
        private String mUrl = "https://api.foursquare.com/v2/venues/";

        public Builder setVersion(final String version) {
            this.mVersion = version;
            return this;
        }

        public Builder setSearchType() {
            this.mSearch = true;
            return this;
        }

        public Builder setClientId(final String clientId) {
            this.mClientId = clientId;
            return this;
        }

        public Builder setPlaceId(final String placeId) {
            this.mPlaceId = placeId;
            return this;
        }

        public Builder setClientKey(final String clientKey) {
            this.mClientKey = clientKey;
            return this;
        }

        public Builder setClientVersion(final String version) {
            this.mVersion = version;
            return this;
        }

        public Builder setLocation(final String latitude, final String longitude) {
            this.mLocation = latitude + "," + longitude;
            return this;
        }

        public FourSquareWebService build() {
            return new FourSquareWebService(this);
        }
    }

}
