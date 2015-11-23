package com.globalfriends.com.aroundme.protocol.places;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
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
    public void findPlaces(PlaceRequestTypeEnum searchType, String placeType, String query) {
        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(searchType).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setRadius(PreferenceManager.getRadius()).
                        setLocation(PreferenceManager.getLocation()).
                        setSensor(false).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        switch (searchType) {
            case SEARCH_TYPE_NEARBY:
                builder.setTypes(placeType);
                break;
            case SEARCH_TYPE_RADAR:
                break;
            case SEARCH_TYPE_TEXT:
                builder.setQuery(query);
                break;
            default:
        }
        handleJasonRequest(builder.build().getUrl(), getClass().getName());
    }

    @Override
    public void findPlacePhoto(String photoReference) {
        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_TYPE_PHOTO).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setPhotoReference(photoReference).
                        setPhotoReference(photoReference).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        handleJasonRequest(builder.build().getUrl(), getClass().getName());
    }

    @Override
    public void findPlaceDetails(String placeId, String contactNumber) {
        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_TYPE_DETAILS).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setPlaceId(placeId).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        handleJasonRequest(builder.build().getUrl(), getClass().getName());
    }
}
