package com.globalfriends.com.aroundme.protocol.places;

import android.text.TextUtils;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.Places;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.protocol.OperationEnum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        handleJsonRequest(builder.build().getUrl(), getClass().getName(), OperationEnum.OPERATION_PLACE_LIST);
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
        handleJsonRequest(builder.build().getUrl(), getClass().getName(), OperationEnum.OPERATION_PLACE_PHOTO);
    }

    @Override
    public void findPlaceDetails(String placeId, String contactNumber) {
        if (TextUtils.isEmpty(placeId)) {
            return;
        }

        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_TYPE_DETAILS).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setPlaceId(placeId).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        handleJsonRequest(builder.build().getUrl(), getClass().getName(), OperationEnum.OPERATION_PLACE_DETAIL);
    }

    /**
     * Schedule response based on provided operation request
     *
     * @param operation
     * @param response
     */
    @Override
    protected void dispatchJsonResponse(final OperationEnum operation, final JSONObject response) {
        switch (operation) {
            case OPERATION_PLACE_DETAIL:
                mListener.onGetPlaceDetails(response, mContext.getString(R.string.google_places_tag));
                break;
            case OPERATION_PLACE_LIST:
                try {
                    JSONArray array = response.getJSONArray("results");
                    List<Places> placeList = new ArrayList<Places>();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            placeList.add(Places
                                    .jsonToPontoReferencia((JSONObject) array.get(i)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mListener.onPlacesList(placeList);
                } catch (JSONException e) {
                    mListener.onError("Jason Parse Exception");
                }
                break;
            case OPERATION_PLACE_PHOTO:
                mListener.onGetPhoto(response, mContext.getString(R.string.google_places_tag));
                break;
            default:
                Logger.e(TAG, ">>>> Invalid operation. Should never come here <<<<");
                mListener.onError("Invalid command type. Internal error");
        }
    }
}
