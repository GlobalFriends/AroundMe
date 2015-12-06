package com.globalfriends.com.aroundme.protocol;

import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by vishal on 11/20/2015.
 */
public interface Listener {
    /**
     * @param response
     */
    void onGetPhoto(JSONObject response, final String placeTag);

    /**
     * @param response
     */
    void onGetPlaceDetails(IPlaceDetails response, final String placeTag);

    /**
     * @param placeList
     */
    void onPlacesList(List<PlaceInfo> placeList);

    /**
     *
     */
    void onError(final String errorMsg, String tag);
}
