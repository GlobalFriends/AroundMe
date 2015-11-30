package com.globalfriends.com.aroundme.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.globalfriends.com.aroundme.data.IPlaceDetails;
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
    private static final String TAG = "TransactionManager";
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

    public void findPlaceDetails(final String placeId, final String phoneNumber) {
        if (TextUtils.isEmpty(placeId) && TextUtils.isEmpty(phoneNumber)) {
            Log.e(TAG, "PlaceId and Phone number both are null or empty");
            for (Result listener : mListeners) {
                listener.onError("Invalid Argument", null);
            }
            return;
        }

        for (IFeatureManager feature : mManagerList) {
            feature.findPlaceDetails(placeId, phoneNumber);
        }
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
    public void onGetPhoto(final JSONObject response, final String placeTag) {
        synchronized (mListeners) {
            for (Result listener : mListeners) {
                listener.onGetPhoto(response, placeTag);
            }
        }
    }

    @Override
    public void onGetPlaceDetails(IPlaceDetails response, String placeTag) {
        synchronized (mListeners) {
            for (Result listener : mListeners) {
                listener.onGetPlaceDetails(response, placeTag);
            }
        }
    }

    @Override
    public void onPlacesList(List<Places> placeList) {
        synchronized (mListeners) {
            for (Result listener : mListeners) {
                listener.onPlacesList(placeList);
            }
        }
    }

    @Override
    public void onError(final String errorMsg, String tag) {
        synchronized (mListeners) {
            for (Result listener : mListeners) {
                listener.onError(errorMsg, tag);
            }
        }
    }

    public void addResultCallback(final Result result) {
        synchronized (mListeners) {
            result.setRegistered(true);
            mListeners.add(result);
        }
    }

    public void removeResultCallback(final Result result) {
        synchronized (mListeners) {
            result.setRegistered(false);
            mListeners.remove(result);
        }

    }

    /**
     * Intermediate hop for callbacks from individual Manager modules.
     * This will be passed to UI elements who ever registered for these.
     */
    public static abstract class Result {
        private boolean mRegistered = false;

        protected final boolean isRegistered() {
            return mRegistered;
        }

        protected void setRegistered(final boolean registered) {
            mRegistered = registered;
        }

        /**
         * @param response
         */
        public void onGetPhoto(JSONObject response, final String placeTag) {
        }

        /**
         * @param response
         */
        public void onGetPlaceDetails(IPlaceDetails response, final String placeTag) {
        }

        /**
         * @param placeList
         */
        public void onPlacesList(List<Places> placeList) {
        }

        public void onError(final String errorMsg, final String tag) {
        }
    }

}
