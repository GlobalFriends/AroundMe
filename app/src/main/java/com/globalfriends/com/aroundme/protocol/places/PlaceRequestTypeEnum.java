package com.globalfriends.com.aroundme.protocol.places;

/**
 * Created by vishal on 11/19/2015.
 */
public enum PlaceRequestTypeEnum {
    SEARCH_TYPE_NEARBY("nearbysearch"),
    SEARCH_TYPE_TEXT("textsearch"),
    SEARCH_TYPE_RADAR("radarsearch"),
    SEARCH_TYPE_PHOTO("photo"),
    SEARCH_TYPE_DETAILS("details"),
    SEARCH_TYPE_AUTOCOMPLETE("autocomplete");

    private String mSearchType;

    PlaceRequestTypeEnum(final String name) {
        this.mSearchType = name;
    }

    public String getSearchType() {
        return this.mSearchType;
    }
}
