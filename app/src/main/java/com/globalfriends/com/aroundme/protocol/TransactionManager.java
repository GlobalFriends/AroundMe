package com.globalfriends.com.aroundme.protocol;

import com.globalfriends.com.aroundme.protocol.places.PlaceManager;
import com.globalfriends.com.aroundme.protocol.yelp.YelpManager;

import org.json.JSONObject;

import java.util.HashSet;

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

    public void nearByPlacesSearchQuery() {
    }

    public void radarSearchQuery() {
    }

    public void textSearchQuery() {
    }

    @Override
    public void onResponse(String response) {
    }

    @Override
    public void onResponse(JSONObject response) {
    }

    @Override
    public void onError() {
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
