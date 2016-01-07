package com.globalfriends.com.aroundme.protocol;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.places.AutoCompletePrediction;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.fourSquare.FourSquareManager;
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
    private HashSet<IFeatureManager> mFeatureManagerList = new HashSet<>();
    private HashSet<Result> mListeners = new HashSet<>();

    private TransactionManager() {
        //Init all transaction managers which needs to be functional
        mFeatureManagerList.add(new YelpManager(this));
        mFeatureManagerList.add(new PlaceManager(this));
        mFeatureManagerList.add(new FourSquareManager(this));
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

        for (IFeatureManager feature : mFeatureManagerList) {
            feature.findGooglePlaceDetails(placeId);
        }
    }

    public void findPlaceDetails(final String internationalNumber, final String phoneNumber,
                                 final Double latitude, final Double longitude) {
        if (TextUtils.isEmpty(internationalNumber)) {
            // Proper telephone is not provided, ignore.
            Log.e(TAG, ">>> findPlaceDetails: No phone number <<<");
            return;
        }

        for (IFeatureManager feature : mFeatureManagerList) {
            feature.findPlaceDetails(internationalNumber, phoneNumber, latitude, longitude);
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

        for (IFeatureManager feature : mFeatureManagerList) {
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
        for (IFeatureManager feature : mFeatureManagerList) {
            mList.put(feature.getTag(), feature.getImageLoader());
        }
        return mList;
    }

    public int getModuleIcon(final String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            return 0;
        }

        for (IFeatureManager feature : mFeatureManagerList) {
            if (feature.getTag().equalsIgnoreCase(moduleName)) {
                return feature.getFeatureIcon();
            }
        }
        return 0;
    }

    public int getModuleCompleteLogo(final String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            return 0;
        }

        for (IFeatureManager feature : mFeatureManagerList) {
            if (feature.getTag().equalsIgnoreCase(moduleName)) {
                return feature.getFeatureFullLogo();
            }
        }
        return 0;
    }

    /**
     * @param placeType
     */
    public void findByNearBy(final String placeType) {
        for (IFeatureManager feature : mFeatureManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_NEARBY, placeType, null);
        }
    }

    /**
     * @param pageToken
     */
    public void findByNearByByPageToken(final String pageToken) {
        for (IFeatureManager feature : mFeatureManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_NEARBY, pageToken);
        }
    }

    /**
     * @param query
     */
    public void findBySearch(final String query) {
        for (IFeatureManager feature : mFeatureManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_TEXT, null, query);
        }
    }

    /**
     * Find places by radar. This is specifically used for finding 200 places with less details
     */
    public void findByRadar() {
        for (IFeatureManager feature : mFeatureManagerList) {
            feature.findPlaces(PlaceRequestTypeEnum.SEARCH_TYPE_RADAR, null, null);
        }
    }

    public void autoComplete(final String input) {
        for (IFeatureManager feature : mFeatureManagerList) {
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
    public void onPlacesList(final String pageToken, List<PlaceInfo> placeList) {
        synchronized (mListeners) {
            for (Result listener : mListeners) {
                listener.onPlacesList(pageToken, placeList);
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
        synchronized (mListeners) {
            result.setRegistered(true);
            mListeners.add(result);
        }
    }

    /**
     * @param result
     */
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
        public void onPlacesList(final String pageToken, List<PlaceInfo> placeList) {
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
