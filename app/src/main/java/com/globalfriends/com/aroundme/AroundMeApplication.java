package com.globalfriends.com.aroundme;

import android.content.Context;

/**
 * Created by vishal on 11/21/2015.
 */
public class AroundMeApplication extends android.app.Application {
    private static Context mContext;
    private final String TAG = getClass().getSimpleName();

    public static Context getContext() {
        return mContext;
    }

    //Test
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
