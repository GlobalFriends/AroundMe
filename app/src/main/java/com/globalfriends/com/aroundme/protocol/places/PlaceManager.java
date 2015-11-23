package com.globalfriends.com.aroundme.protocol.places;

import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;

/**
 * Created by vishal on 11/19/2015.
 */
public class PlaceManager extends DefaultFeatureManager {
    public PlaceManager(final Listener listener) {
        super(listener);
    }

    @Override
    public void findPlaces(PlaceSearchTypeEnum searchType, String placeType, String query) {
//        new PlacesWebService.Builder().
//                setSearchType(searchType).
//                setResponseType(PlaceResponseEnum.RESP_JSON).
//                setRadius(PreferenceManager.getRadius()).
    }

    @Override
    public void findPlaceDetails(String placeId, String contactNumber) {
    }


}
