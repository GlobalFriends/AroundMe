package com.globalfriends.com.aroundme.protocol;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.protocol.places.PlaceErrorDescription;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;
import com.globalfriends.com.aroundme.utils.Utility;

import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class DefaultFeatureManager implements IFeatureManager {
    protected String TAG;
    protected final String STATUS_OK = "OK";
    protected final String STATUS = "status";
    protected Listener mListener;
    protected Context mContext;
    protected String mModuleTag;
    private RequestQueue mQueue;
    private Request mRequest;
    private ImageLoader mImageLoader;

    /**
     * @param listener
     * @param tag
     */
    public DefaultFeatureManager(final Listener listener, final String tag) {
        mContext = AroundMeApplication.getContext();
        mListener = listener;
        mModuleTag = tag;
        TAG = getClass().getSimpleName();
        mQueue = Volley.newRequestQueue(AroundMeApplication.getContext());
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache());
        mQueue.start();
    }

    /**
     * Check if network is present or not
     *
     * @return
     */
    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    /**
     * Schedule a request
     *
     * @param request
     * @param <T>
     */
    final <T> void scheduleRequest(Request<T> request) {
        mQueue.add(request);
    }

    public final void sendVolleyJsonRequest(final String url, final OperationEnum operation) {
        if (!isNetworkAvailable()) {
            mListener.onError(mContext.getString(R.string.network_error), mModuleTag);
            return;
        }

        mRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dispatchJsonResponse(operation, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (Utility.DEBUG) {
                            Log.i(TAG, "onErrorResponse=" + (error == null ? null : error.toString()));
                        }

                        if (mContext.getString(R.string.google_places_tag).equalsIgnoreCase(mModuleTag)) {
                            mListener.onError(PlaceErrorDescription.getErrorString(mContext, error.toString()), mModuleTag);
                        } else {
                            mListener.onError(error.toString(), mModuleTag);
                        }

                    }
                });
        if (Utility.DEBUG) {
            Log.i(TAG, "mRequest=" + mRequest);
        }
        scheduleRequest(mRequest);
    }

    /**
     * Schedule response based on provided operation request
     *
     * @param operation
     * @param response
     */
    protected void dispatchJsonResponse(final OperationEnum operation, final JSONObject response) {
    }

    @Override
    public void findPlaces(PlaceRequestTypeEnum searchType, String placeType, String query) {
        // Will be implemented by PlaceManager API's
    }

    @Override
    public void findPlaces(PlaceRequestTypeEnum searchType, String pageToken) {

    }

    @Override
    public void findGooglePlaceDetails(String placeId) {
    }

    @Override
    public void findPlaceDetails(String internationalPhone, String phoneNumber, Double latitude, Double longitude) {
        //
    }

    @Override
    public void placeAutoComplete(String input) {
        //
    }

    @Override
    public void queryAutoComplete(String input) {

    }

    @Override
    public final ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public String getTag() {
        return mModuleTag;
    }

    @Override
    public int getFeatureIcon() {
        return 0;
    }

    public int getFeatureFullLogo() {
        return 0;
    }

    @Override
    public boolean isCustomRating() {
        return false;
    }
}
