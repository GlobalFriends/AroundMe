package com.globalfriends.com.aroundme.protocol;

import com.globalfriends.com.aroundme.data.places.Places;
import com.globalfriends.com.aroundme.protocol.places.PlaceManager;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;
import com.globalfriends.com.aroundme.protocol.yelp.YelpManager;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;

/**
 * Created by vishal on 11/19/2015.
 */
public class TransactionManager implements Listener {
    private static TransactionManager sInstance = null;
    private HashSet<IFeatureManager> mManagerList = new HashSet<>();
    private HashSet<Result> mListeners = new HashSet<>();

    private TransactionManager() {
        //Init all transaction managers which needs to be functional
        mManagerList.add(new YelpManager(this));
        mManagerList.add(new PlaceManager(this));
    }

    /**
     * @return
     */
    public static TransactionManager getInstance() {
        if (sInstance == null) {
            sInstance = new TransactionManager();
        }
        return sInstance;
    }

    /**
     * @param placeType
     */
    public void findByNearBy(final String placeType) {
        for (IFeatureManager feature : mManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_NEARBY, placeType, null);
        }
    }

    /**
     * @param query
     */
    public void findBySearch(final String query) {
        for (IFeatureManager feature : mManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_TEXT, null, query);
        }
    }

    /**
     * Find places by radar. This is specifically used for finding 200 places with less details
     */
    public void findByRadar() {
        for (IFeatureManager feature : mManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_RADAR, null, null);
        }
    }

    @Override
    public void onGetPhoto(JSONObject response, String placeTag) {

    }

    @Override
    public void onGetPlaceDetails(JSONObject response, String placeTag) {

    }

    @Override
    public void onPlacesList(List<Places> placeList) {

    }

    @Override
    public void onError(final String errorMsg) {
    }

    /**
     * Intermediate hop for callbacks from individual Manager modules.
     * This will be passed to UI elements who ever registered for these.
     */
    class Result {
        public void registerListener(final Result result) {
            synchronized (mListeners) {
                mListeners.add(result);
            }
        }

        public void unregisterListener(final Result result) {
            synchronized (mListeners) {
                mListeners.remove(result);
            }

        }
    }

}
