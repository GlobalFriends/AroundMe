package com.globalfriends.com.aroundme.ui;

/**
 * Created by Vishal on 12/19/2015.
 */
public interface ToolbarUpdateListener {

    public void onNavigationEnabled(final boolean visibility);

    public void onSearchBarEnabled(final boolean visibility);

    public void settingsOptionUpdate(final boolean visibility);
}
