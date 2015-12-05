package com.globalfriends.com.aroundme.protocol.yelp;

import android.text.TextUtils;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.yelp.YelpDetailsJson;
import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class YelpManager extends DefaultFeatureManager {

    public YelpManager(final Listener listener) {
        super(listener, AroundMeApplication.getContext().getResources().getString(R.string.yelp_consumer_key));
    }

    @Override
    public void findPlaceDetails(String placeId, String contactNumber) {
        if (TextUtils.isEmpty(contactNumber)) {
            return; // Not meant for this
        }
        String response = Yelp.getYelp(mContext).phoneSearch(contactNumber, Utility.getCountryCodeFromLocation());
        Utility.generateNoteOnSD("placeDetails_yelp", response.toString());
        try {
            IPlaceDetails placeDetails = new YelpDetailsJson(new JSONObject(response));
            mListener.onGetPlaceDetails(placeDetails, mContext.getString(R.string.yelp_tag));
        } catch (JSONException e) {
            e.printStackTrace();
            mListener.onError("Response Error", mContext.getString(R.string.yelp_tag));
        }
    }
}
