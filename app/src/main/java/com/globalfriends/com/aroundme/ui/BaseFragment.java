package com.globalfriends.com.aroundme.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Vishal on 12/19/2015.
 */
public class BaseFragment extends Fragment {
    protected ToolbarUpdateListener mToolbarUpdater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mToolbarUpdater = (ToolbarUpdateListener) context;
    }
}
