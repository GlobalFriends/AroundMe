package com.globalfriends.com.aroundme.protocol;

import com.android.volley.toolbox.ImageLoader;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;

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
    void findPlaces(final PlaceRequestTypeEnum searchType, String placeType, String query);

    /**
     * @param searchType
     * @param pageToken
     */
    void findPlaces(final PlaceRequestTypeEnum searchType, final String pageToken);

    /**
     * Return place details based on provided unique identifications
     *
     * @param placeId Unique identification for Google PlaceInfo
     * @return
     */
    void findGooglePlaceDetails(final String placeId);

    /**
     * Returns place details based on unique phone number
     *
     * @param phoneNumber
     */
    void findPlaceDetails(final String phoneNumber);

    /**
     * Do an autocomplete query based on input text
     *
     * @param input The autocomplete text
     */
    void autoComplete(final String input);

    /**
     * Returns Volley Image Loader
     *
     * @return
     */
    ImageLoader getImageLoader();

    /**
     * Returns modules specific tag
     *
     * @return
     */
    public String getTag();

    /**
     * @return
     */
    public int getFeatureIcon();
}
