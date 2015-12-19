package com.globalfriends.com.aroundme.protocol.places;

import android.text.TextUtils;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.AutoCompletePrediction;
import com.globalfriends.com.aroundme.data.places.GooglePlaceDetailsJson;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.protocol.OperationEnum;
import com.globalfriends.com.aroundme.utils.Utility;

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
        super(listener, AroundMeApplication.getContext().getResources().getString(R.string.google_places_tag));
    }

    @Override
    public void findPlaces(PlaceRequestTypeEnum searchType, String placeType, String query) {
        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(searchType).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setLocation(PreferenceManager.getLocation()).
                        setRankBy(PlacesRankByEnum.RANK_BY_DISTANCE).
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
        sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_LIST);
    }


    @Override
    public void findPlaces(PlaceRequestTypeEnum searchType, String pageToken) {
        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(searchType).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setPageToken(pageToken).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_LIST);
    }

    @Override
    public void findGooglePlaceDetails(String placeId) {
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
        sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_DETAIL);
    }

    @Override
    public void autoComplete(String input) {
        if (TextUtils.isEmpty(input)) {
            return;
        }

        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_TYPE_AUTOCOMPLETE).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setInput(input).
                        setKey(AroundMeApplication.getContext().getResources().getString(R.string.google_maps_key));

        sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_AUTOCOMPLETE);
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
                Utility.generateNoteOnSD("placeDetails_googles", response.toString());
                try {
                    if (response.has(STATUS)) {
                        if (STATUS_OK.equalsIgnoreCase(response.getString(STATUS))) {
                            JSONObject result = response.getJSONObject("result");
                            IPlaceDetails placeDetails = new GooglePlaceDetailsJson(result);
                            mListener.onGetPlaceDetails(placeDetails, mContext.getString(R.string.google_places_tag));
                        } else {
                            mListener.onError(response.getString(STATUS), mContext.getString(R.string.google_places_tag));
                        }
                    }
                } catch (JSONException e) {
                    mListener.onError("Jason Parse Exception", mModuleTag);
                }
                break;
            case OPERATION_PLACE_LIST:
                Utility.generateNoteOnSD("placeList_google", response.toString());
                try {
                    if (response.has(STATUS)) {
                        if (STATUS_OK.equalsIgnoreCase(response.getString(STATUS))) {
                            JSONArray array = response.getJSONArray("results");
                            List<PlaceInfo> placeList = new ArrayList<PlaceInfo>();
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    placeList.add(PlaceInfo
                                            .jsonToPontoReferencia((JSONObject) array.get(i)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            String pageToken = null;
                            if (response.has("next_page_token")) {
                                pageToken = response.getString("next_page_token");
                            }
                            mListener.onPlacesList(pageToken, placeList);
                        } else {
                            mListener.onError(response.getString(STATUS), mContext.getString(R.string.google_places_tag));
                        }
                    }
                } catch (JSONException e) {
                    mListener.onError("Jason Parse Exception", mModuleTag);
                }
                break;
            case OPERATION_PLACE_PHOTO:
                mListener.onGetPhoto(response, mModuleTag);
                break;
            case OPERATION_AUTOCOMPLETE:
                mListener.onAutoComplete(AutoCompletePrediction.parse(response));
                break;
            default:
                Logger.e(LOGGING_TAG, ">>>> Invalid operation. Should never come here <<<<");
                mListener.onError("Invalid command type. Internal error", mModuleTag);
        }
    }

    @Override
    public int getFeatureIcon() {
        return R.drawable.google;
    }
}
