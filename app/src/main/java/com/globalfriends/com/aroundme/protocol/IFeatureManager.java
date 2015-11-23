package com.globalfriends.com.aroundme.protocol;

import com.globalfriends.com.aroundme.protocol.places.PlaceSearchTypeEnum;

/**
 * Created by vishal on 11/19/2015.
 */
public interface IFeatureManager {
    /**
     * Find details of places
     *
     * @param searchType
     * @param placeType
     * @param query      @return
     */
    void findPlaces(final PlaceSearchTypeEnum searchType, String placeType, String query);

    /**
     * Return place details based on provided unique identifications
     *
     * @param placeId       Unique identification for Google Places
     * @param contactNumber unique identification for Yelp
     * @return
     */
    void findPlaceDetails(final String placeId, final String contactNumber);
}
