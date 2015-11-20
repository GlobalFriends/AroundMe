package com.globalfriends.com.aroundme.protocol;

import com.globalfriends.com.aroundme.protocol.places.PlaceManager;
import com.globalfriends.com.aroundme.protocol.yelp.YelpManager;

import java.util.HashSet;

/**
 * Created by vishal on 11/19/2015.
 */
public class TransactionManager {
    private static TransactionManager sInstance = null;
    private HashSet<IFeatureManager> mManagerList = new HashSet<>();

    private TransactionManager() {
        //Init all transaction managers which needs to be functional
        mManagerList.add(new YelpManager());
        mManagerList.add(new PlaceManager());
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
}
