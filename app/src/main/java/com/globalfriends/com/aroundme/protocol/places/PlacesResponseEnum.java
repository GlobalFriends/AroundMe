package com.globalfriends.com.aroundme.protocol.places;

/**
 * Created by vishal on 11/19/2015.
 */
public enum PlacesResponseEnum {
    RESP_XML("xml"), RESP_JSON("json");

    private String mName;

    PlacesResponseEnum(final String name) {
        this.mName = name;
    }

    public String getResponseType() {
        return this.mName;
    }
}
