package com.globalfriends.com.aroundme.protocol;

import org.json.JSONObject;

/**
 * Created by vishal on 11/20/2015.
 */
public interface Listener {
    void onResponse(String response);

    void onResponse(JSONObject response);

    void onError();
}
