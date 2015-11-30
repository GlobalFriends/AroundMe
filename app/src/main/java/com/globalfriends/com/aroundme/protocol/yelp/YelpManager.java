package com.globalfriends.com.aroundme.protocol.yelp;

import android.text.TextUtils;

import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.utils.Utility;

/**
 * Created by vishal on 11/19/2015.
 */
public class YelpManager extends DefaultFeatureManager {

    public YelpManager(final Listener listener) {
        super(listener);
    }

    @Override
    public void findPlaceDetails(String placeId, String contactNumber) {
        if (TextUtils.isEmpty(contactNumber)) {
            return; // Not meant for this
        }
        String response = Yelp.getYelp(mContext).phoneSearch(contactNumber, Utility.getCountryCodeFromLocation());
    }
}
