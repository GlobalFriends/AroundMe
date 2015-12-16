package com.globalfriends.com.aroundme.protocol.yelp;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.yelp.YelpPlaceDetailsJson;
import com.globalfriends.com.aroundme.protocol.DefaultFeatureManager;
import com.globalfriends.com.aroundme.protocol.Listener;
import com.globalfriends.com.aroundme.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class YelpManager extends DefaultFeatureManager {
    private static final int PLACE_DETAIL_REQUEST = 1;
    private static final int PLACE_LIST_REQUEST = 2;

    public YelpManager(final Listener listener) {
        super(listener, AroundMeApplication.getContext().getResources().getString(R.string.yelp_tag));
    }

    @Override
    public void findPlaceDetails(String placeId, String contactNumber) {
        if (TextUtils.isEmpty(contactNumber)) {
            return; // Not meant for this
        }

        new YelpNetworkTask(contactNumber, PLACE_DETAIL_REQUEST).execute();
    }

    /**
     * Network executor task. Currently it supports only Search by phone number network task.
     */
    class YelpNetworkTask extends AsyncTask<Void, Void, String> {
        private String mPhoneNumber;
        private int mPlaceType;

        YelpNetworkTask(final String phone, final int requestType) {
            mPhoneNumber = phone;
            mPlaceType = requestType;
        }

        @Override
        protected String doInBackground(Void... params) {
            return Yelp.getYelp(mContext).phoneSearch(mPhoneNumber, Utility.getCountryCodeFromLocation());
        }

        @Override
        protected void onPostExecute(final String response) {
            super.onPostExecute(response);
            if (TextUtils.isEmpty(response)) {
                mListener.onError(mContext.getString(R.string.invalid_response),
                        mModuleTag);
                return;
            }
            Utility.generateNoteOnSD("placeDetails_yelp", response.toString());

            try {
                IPlaceDetails placeDetails = new YelpPlaceDetailsJson(new JSONObject(response));
                mListener.onGetPlaceDetails(placeDetails, mContext.getString(R.string.yelp_tag));
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.onError(mContext.getString(R.string.failed_response), mContext.getString(R.string.yelp_tag));
            }
        }
    }
}
