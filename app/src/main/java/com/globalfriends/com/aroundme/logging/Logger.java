package com.globalfriends.com.aroundme.logging;

import android.util.Log;

/**
 * Created by anup on 11/10/15.
 */
public class Logger {
    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }
}
