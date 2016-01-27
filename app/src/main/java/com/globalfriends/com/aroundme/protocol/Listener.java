package com.globalfriends.com.aroundme.protocol;

import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.places.AutoCompletePrediction;
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
     * @param pageToken
     * @param placeList
     */
    void onPlacesList(final String pageToken, List<PlaceInfo> placeList);

    /**
     *
     * @param predictions
     */
    void onPlaceAutoComplete(List<AutoCompletePrediction> predictions);

    /**
     *
     * @param predictions
     */
    void onQueryAutoComplete(List<AutoCompletePrediction> predictions);

    /**
     *
     */
    void onError(final String errorMsg, String tag);
}
