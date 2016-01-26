package com.globalfriends.com.aroundme.protocol.places;

/**
 * Created by Vishal on 1/26/2016.
 */
public enum AutoCompletePlaceTypeEnum {
    AUTOCOMPLETE_PLACE_TYPE_GEOCODE("geocode"),
    AUTOCOMPLETE_PLACE_TYPE_ADDRESS("address"),
    AUTOCOMPLETE_PLACE_TYPE_ESTABLISHMENT("establishment"),
    AUTOCOMPLETE_PLACE_TYPE_REGIONS("(regions)"),
    AUTOCOMPLETE_PLACE_TYPE_CITIES("(cities)");

    private String mPlaceType;

    AutoCompletePlaceTypeEnum(final String name) {
        this.mPlaceType = name;
    }

    public String getAutoCompletePlaceType() {
        return this.mPlaceType;
    }
}
