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
import java.util.TreeMap;

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
                        setLanguage(PreferenceManager.getPreferredLanguage()).
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
                        setLanguage(PreferenceManager.getPreferredLanguage()).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        sendVolleyJsonRequest(builder.build().getUrl(), OperationEnum.OPERATION_PLACE_DETAIL);
    }

    @Override
    public void placeAutoComplete(String input) {
        if (TextUtils.isEmpty(input)) {
            return;
        }

        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_PLACE_AUTOCOMPLETE).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setAutoCompletePlaceType(AutoCompletePlaceTypeEnum.AUTOCOMPLETE_PLACE_TYPE_REGIONS).
                        setLanguage(PreferenceManager.getPreferredLanguage()).
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
                Utility.generateNoteOnSD("placeDetails_google", response.toString());
                try {
                    if (response.has(STATUS)) {
                        if (STATUS_OK.equalsIgnoreCase(response.getString(STATUS))) {
                            JSONObject result = response.getJSONObject("result");
                            IPlaceDetails placeDetails = new GooglePlaceDetailsJson(result);
                            mListener.onGetPlaceDetails(placeDetails, mContext.getString(R.string.google_places_tag));
                        } else {
                            mListener.onError(PlaceErrorDescription.getErrorString(mContext, response.getString(STATUS)),
                                    mContext.getString(R.string.google_places_tag));
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
                            List<PlaceInfo> placeList = getSortedList(response.getJSONArray("results"));

                            String pageToken = null;
                            if (response.has("next_page_token")) {
                                pageToken = response.getString("next_page_token");
                            }
                            mListener.onPlacesList(pageToken, placeList);
                        } else {
                            mListener.onError(PlaceErrorDescription.getErrorString(mContext, response.getString(STATUS)),
                                    mContext.getString(R.string.google_places_tag));
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
                Logger.e(TAG, ">>>> Invalid operation. Should never come here <<<<");
                mListener.onError("Invalid command type. Internal error", mModuleTag);
        }
    }

    /**
     * Returns a sorted result list
     *
     * @param array
     * @return
     */
    private List<PlaceInfo> getSortedList(JSONArray array) {
        TreeMap<Double, PlaceInfo> placeMap = new TreeMap<Double, PlaceInfo>();
        boolean ratedResults = PreferenceManager.getRatedOnlySelection();
        for (int i = 0; i < array.length(); i++) {
            try {
                PlaceInfo placeInfo = PlaceInfo
                        .jsonToPontoReferencia((JSONObject) array.get(i));
                if (ratedResults) {
                    if (Utility.getDefaultDouble(placeInfo.getRating()) > 0d) {
                        placeMap.put(Utility.distanceFromLatitudeLongitudeInMeters(Double.valueOf(PreferenceManager.getLatitude()),
                                Double.valueOf(PreferenceManager.getLongitude()),
                                placeInfo.getLatitude(),
                                placeInfo.getLongitude(),
                                PreferenceManager.getDistanceFormat()), placeInfo);
                    }
                } else {
                    placeMap.put(Utility.distanceFromLatitudeLongitudeInMeters(Double.valueOf(PreferenceManager.getLatitude()),
                            Double.valueOf(PreferenceManager.getLongitude()),
                            placeInfo.getLatitude(),
                            placeInfo.getLongitude(),
                            PreferenceManager.getDistanceFormat()), placeInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<PlaceInfo>(placeMap.values());
    }


    /*
    private List<PlaceInfo> getSortedList(JSONArray array) {
        boolean sortByDistance = mContext.getString(R.string.sorting_distance).equalsIgnoreCase(PreferenceManager.getPreferredSorting());
        TreeMap<Double, List<PlaceInfo>> placeMap = new TreeMap<Double, List<PlaceInfo>>();
        for (int i = 0; i < array.length(); i++) {
            try {
                PlaceInfo placeInfo = PlaceInfo
                        .jsonToPontoReferencia((JSONObject) array.get(i));
                Double key = 0d;
                if (sortByDistance) {
                    key = Utility.distanceFromLatitudeLongitudeInMeters(Double.valueOf(PreferenceManager.getLatitude()),
                            Double.valueOf(PreferenceManager.getLongitude()),
                            placeInfo.getLatitude(),
                            placeInfo.getLongitude(),
                            PreferenceManager.getDistanceFormat());
                } else {
                    key = Utility.getDefaultDouble(placeInfo.getRating());
                }

                if (placeMap.containsKey(key)) {
                    placeMap.get(key).add(placeInfo);
                } else {
                    List<PlaceInfo> list = new ArrayList<>();
                    list.add(placeInfo);
                    placeMap.put(key, list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        ArrayList<PlaceInfo> infoArrayList = new ArrayList<PlaceInfo>();
        for(Double key: placeMap.keySet()){
            List<PlaceInfo> list = placeMap.get(key);
            if (!sortByDistance) {
                Collections.reverse(list);
            }
            for (PlaceInfo content : list) {
                infoArrayList.add(content);
            }
        }

        if (!sortByDistance) {
            Collections.reverse(infoArrayList);
        }
        return infoArrayList;
    }*/

    @Override
    public int getFeatureIcon() {
        return R.drawable.google;
    }

    public int getFeatureFullLogo() {
        return R.drawable.google_logo;
    }
}
