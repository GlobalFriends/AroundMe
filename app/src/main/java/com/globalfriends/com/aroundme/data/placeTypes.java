package com.globalfriends.com.aroundme.data;

/**
 * Created by vishal on 11/14/2015.
 */
public class placeTypes {
    private String mName;
    private String mIntent;
    private int mIconId;
    private String mDescription;

    public placeTypes(final String name, final String intent,
                      final int iconId, final String description) {
        this.mName = name;
        this.mIntent = intent;
        this.mIconId = iconId;
        this.mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public String getIntent() {
        return mIntent;
    }

    public int getIcon() {
        return mIconId;
    }

    public String getDescription() {
        return mDescription;
    }

}
