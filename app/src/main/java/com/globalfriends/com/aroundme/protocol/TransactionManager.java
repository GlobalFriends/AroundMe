package com.globalfriends.com.aroundme.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.places.AutoCompletePrediction;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.places.PlaceManager;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;
import com.globalfriends.com.aroundme.protocol.yelp.YelpManager;

import org.json.JSONObject;

import java.util.HashMap;
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

    /**
     * @param placeId
     * @param phoneNumber
     */
    public void findGooglePlaceDetails(final String placeId, final String phoneNumber) {
        if (TextUtils.isEmpty(placeId) && TextUtils.isEmpty(phoneNumber)) {
            Log.e(TAG, "PlaceId and Phone number both are null or empty");
            for (Result listener : mListeners) {
                listener.onError("Invalid Argument", null);
            }
            return;
        }

        for (IFeatureManager feature : mManagerList) {
            feature.findGooglePlaceDetails(placeId);
        }
    }

    public void findPlaceDetails(final String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            // Proper telephone is not provided, ignore.
            Log.e(TAG, ">>> findGooglePlaceDetails: No phone number <<<");
            return;
        }

        for (IFeatureManager feature : mManagerList) {
            feature.findPlaceDetails(phoneNumber);
        }
    }

    /**
     * Returns specific image loader based on Module key
     *
     * @param moduleKey
     * @return
     */
    public ImageLoader getModuleImageLoader(final String moduleKey) {
        if (TextUtils.isEmpty(moduleKey)) {
            return null;
        }

        for (IFeatureManager feature : mManagerList) {
            if (moduleKey.equalsIgnoreCase(feature.getTag())) {
                return feature.getImageLoader();
            }
        }
        return null;
    }

    /**
     * @return
     */
    public HashMap<String, ImageLoader> getModuleImageLoaders() {
        HashMap<String, ImageLoader> mList = new HashMap();
        for (IFeatureManager feature : mManagerList) {
            mList.put(feature.getTag(), feature.getImageLoader());
        }
        return mList;
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

    public void autoComplete(final String input) {
        for (IFeatureManager feature : mManagerList) {
            feature.autoComplete(input);
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
    public void onPlacesList(List<PlaceInfo> placeList) {
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

    @Override
    public void onAutoComplete(List<AutoCompletePrediction> predictions) {
        synchronized (mListeners) {
            for (Result listener : mListeners) {
                listener.onAutoComplete(predictions);
            }
        }
    }

    /**
     * @param result
     */
    public void addResultCallback(final Result result) {
        if (result.isRegistered()) {
            return;
        }
        synchronized (mListeners) {
            result.setRegistered(true);
            mListeners.add(result);
        }
    }

    /**
     * @param result
     */
    public void removeResultCallback(final Result result) {
        if (!result.isRegistered()) {
            return;
        }
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

        /**
         * @return
         */
        protected final boolean isRegistered() {
            return mRegistered;
        }

        /**
         * @param registered
         */
        protected void setRegistered(final boolean registered) {
            mRegistered = registered;
        }

        /**
         * @param response
         * @param placeTag
         */
        public void onGetPhoto(JSONObject response, final String placeTag) {
        }

        /**
         * @param response
         * @param placeTag
         */
        public void onGetPlaceDetails(IPlaceDetails response, final String placeTag) {
        }

        /**
         * @param placeList
         */
        public void onPlacesList(List<PlaceInfo> placeList) {
        }

        public void onAutoComplete(List<AutoCompletePrediction> predictions) {
        }

        /**
         * @param errorMsg
         * @param tag
         */
        public void onError(final String errorMsg, final String tag) {
        }
    }

}
