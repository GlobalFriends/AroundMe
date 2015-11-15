package com.globalfriends.com.aroundme.Logging;

import android.util.Log;

/**
 * Created by vishal on 11/15/2015.
 */
public class Logger {
    private static final String TAG = "AroundMe";

    public static void i(final String tag, final String message) {
        Log.i(TAG + "tag", message);
    }

    public static void e(final String tag, final String message) {
        Log.e(TAG + "tag", message);
    }

    public static void v(final String tag, final String message) {
        Log.v(TAG + "tag", message);
    }
}
