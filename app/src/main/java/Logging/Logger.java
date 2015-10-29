package Logging;

import android.util.Log;

/**
 * Created by vishal on 10/27/2015.
 */
public class Logger {
    private static final String TAG = "AroundMeLogger:";

    public static void i(final String tag, final String message) {
        Log.i(TAG + tag, message);
    }
}
