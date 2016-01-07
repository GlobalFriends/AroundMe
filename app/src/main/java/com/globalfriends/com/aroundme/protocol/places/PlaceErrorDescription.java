package com.globalfriends.com.aroundme.protocol.places;

import android.content.Context;

import com.globalfriends.com.aroundme.R;

import java.util.HashMap;

/**
 * Created by Vishal on 1/6/2016.
 */
public class PlaceErrorDescription {
    private static final String ZERO_RESULTS = "ZERO_RESULTS";
    private static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    private static final String REQUEST_DENIED = "REQUEST_DENIED";
    private static final String INVALID_REQUEST = "INVALID_REQUEST";

    private static final HashMap<String, Integer> mErrorMap;

    static {
        mErrorMap = new HashMap<String, Integer>();
        mErrorMap.put(ZERO_RESULTS, R.string.error_zero_result);
        mErrorMap.put(OVER_QUERY_LIMIT, R.string.error_query_limit);
        mErrorMap.put(REQUEST_DENIED, R.string.error_request_denied);
        mErrorMap.put(INVALID_REQUEST, R.string.error_invalid_request);
    }

    public static String getErrorString(final Context context, final String errorCode) {
        if (mErrorMap.containsKey(errorCode)) {
            return context.getString(mErrorMap.get(errorCode));
        }

        return context.getString(R.string.error_unknown_error);
    }
}
