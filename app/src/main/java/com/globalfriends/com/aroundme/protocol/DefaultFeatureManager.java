package com.globalfriends.com.aroundme.protocol;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;

import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class DefaultFeatureManager implements IFeatureManager {
    protected static String LOGGING_TAG;
    protected final String STATUS_OK = "OK";
    protected final String STATUS = "status";
    protected Listener mListener;
    protected Context mContext;
    private RequestQueue mQueue;
    private Request mRequest;
    private ImageLoader mImageLoader;
    protected String mModuleTag;

    /**
     * @param listener
     * @param tag
     */
    public DefaultFeatureManager(final Listener listener, final String tag) {
        mContext = AroundMeApplication.getContext();
        mListener = listener;
        mModuleTag = tag;
        LOGGING_TAG = getClass().getSimpleName();
        mQueue = Volley.newRequestQueue(AroundMeApplication.getContext());
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache());
        mQueue.start();
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
        Log.i(LOGGING_TAG, "Url=" + url);
        mRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOGGING_TAG, "response=" + response);
                        dispatchJsonResponse(operation, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOGGING_TAG, "response=" + error.toString());
                        mListener.onError(error.toString(), mModuleTag);
                    }
                });
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
    public void findGooglePlaceDetails(String placeId) {
    }

    @Override
    public void findPlaceDetails(String phoneNumber) {
        //
    }

    @Override
    public void autoComplete(String input) {
        //
    }

    @Override
    public final ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public String getTag() {
        return mModuleTag;
    }
}
