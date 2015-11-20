package com.globalfriends.com.aroundme.protocol;

/**
 * Created by vishal on 11/19/2015.
 */
public enum PlaseSearcHTypeEnum {
    SEARCH_TYPE_NEARBY("nearbysearch"),
    SEARCH_TYPE_TEXT("textsearch"),
    SEARCH_TYPE_RADAR("radarsearch");

    private String mSearchType;

    PlaseSearcHTypeEnum(final String name) {
        this.mSearchType = name;
    }

    public String getResponseType() {
        return this.mSearchType;
    }
}
