package com.globalfriends.com.aroundme.protocol;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;

import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class DefaultFeatureManager implements IFeatureManager {
    protected static String TAG;
    protected Listener mListener;
    protected Context mContext;
    private RequestQueue mQueue;
    private Request mRequest;

    /**
     * @param listener
     */
    public DefaultFeatureManager(final Listener listener) {
        mListener = listener;
        TAG = getClass().getSimpleName();
        mQueue = Volley.newRequestQueue(AroundMeApplication.getContext());
        mContext = AroundMeApplication.getContext();
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

    public final void handleJsonRequest(final String url, final String tag, final OperationEnum operation) {
        Log.i(TAG, "Url=" + url);
        mRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "response=" + response);
                        dispatchJsonResponse(operation, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "response=" + error.toString());
                        mListener.onError(error.toString(), tag);
                    }
                });
        mRequest.setTag(tag);
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
    public void findPlaceDetails(String placeId, String contactNumber) {
    }

    @Override
    public void findPlacePhoto(String photoReference) {
    }
}
