package com.globalfriends.com.aroundme.protocol;

/**
 * Created by vishal on 11/19/2015.
 */
public class DefaultFeatureManager implements IFeatureManager {
    Listener mListener;

    public DefaultFeatureManager(final Listener listener) {
        mListener = listener;
    }
}
