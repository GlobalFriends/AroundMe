package com.globalfriends.com.aroundme.utils;

import com.google.android.gms.location.places.PlaceTypes;

import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vishal on 11/14/2015.
 */
public class PlaceTypeParser {
    private static final String TAG_PLACE_TYPE = "placeType";
    private static final String TAG_PLACE_NAME = "name";
    private static final String TAG_PLACE_INTENT = "intent";
    private static final String TAG_PLACE_DESCRIPTION = "description";
    //Let it be in memory...
    private static List<PlaceTypes> mList = new ArrayList<PlaceTypes>();
    public volatile boolean parsingComplete = true;
    private XmlPullParserFactory mXmlFactory;

    public PlaceTypeParser() {
    }

    public List<PlaceTypes> getPlaceTypeDetails() {
        if (mList != null && mList.size() != 0) {
            return mList;
        }

        // List size is 0..lets populate it
        return mList;
    }
}
