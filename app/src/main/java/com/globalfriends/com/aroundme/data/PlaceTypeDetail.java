package com.globalfriends.com.aroundme.data;

/**
 * Created by vishal on 11/14/2015.
 */
public class PlaceTypeDetail {
    private String mName;
    private String mIntent;
    private int mIconId;
    private String mDescription;

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        this.mName = name;
    }

    public String getIntent() {
        return mIntent;
    }

    public void setIntent(final String intent) {
        this.mIntent = intent;
    }

    public int getIcon() {
        return mIconId;
    }

    public void setIcon(final int iconId) {
        this.mIconId = iconId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(final String description) {
        this.mDescription = description;
    }

}
