package com.globalfriends.com.aroundme.protocol;

import org.json.JSONObject;

/**
 * Created by vishal on 11/20/2015.
 */
public interface Listener {
    void onPlaceDetailsResponse(String response);

    void onPlaceDetailsResponse(JSONObject response);

    void onRequestError();
}
