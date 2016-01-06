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

    public YelpManager(final Listener listener) {
        super(listener, AroundMeApplication.getContext().getResources().getString(R.string.yelp_tag));
    }

    @Override
    public void findPlaceDetails(final String internationalNumber, final String contactNumber,
                                 Double latitude, Double longitude) {
        if (TextUtils.isEmpty(contactNumber) && TextUtils.isEmpty(internationalNumber)) {
            return; // Not meant for this
        }

        if (!isNetworkAvailable()) {
            mListener.onError(mContext.getString(R.string.network_error), mModuleTag);
            return;
        }

        if (!TextUtils.isEmpty(contactNumber)) {
            new YelpNetworkTask(contactNumber, PLACE_DETAIL_REQUEST).execute();
            return;
        }

        new YelpNetworkTask(internationalNumber, PLACE_DETAIL_REQUEST).execute();
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
                JSONObject resp = new JSONObject(response);
                if (resp.has("error")) {
                    // Error handling as mentioned in https://www.yelp.com/developers/documentation/v2/errors
                    JSONObject val = resp.getJSONObject("error");
                    mListener.onError(val.getString("text"), mContext.getString(R.string.yelp_tag));
                } else {
                    IPlaceDetails placeDetails = new YelpPlaceDetailsJson(resp);
                    mListener.onGetPlaceDetails(placeDetails, mContext.getString(R.string.yelp_tag));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.onError(mContext.getString(R.string.failed_response), mContext.getString(R.string.yelp_tag));
            }
        }
    }

    @Override
    public int getFeatureIcon() {
        return R.drawable.yelp;
    }

    @Override
    public int getFeatureFullLogo() {
        return R.drawable.yelp_icon;
    }
}
