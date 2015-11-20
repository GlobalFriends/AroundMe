package com.globalfriends.com.aroundme.data.places;

import android.content.Context;

import com.globalfriends.com.aroundme.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by vishal on 11/14/2015.
 */
public class PlaceTypeParser {
    private static final String TAG_PLACE_TYPE = "placeType";
    private static final String TAG_PLACE_NAME = "name";
    private static final String TAG_PLACE_INTENT = "intent";
    private static final String TAG_PLACE_DESCRIPTION = "description";
    private static final String TAG_PLACE_ICON = "icon";

    private Context mContext;
    private ArrayList<PlaceTypeDetail> mList = new ArrayList<PlaceTypeDetail>();
    private String text;

    public PlaceTypeParser(Context context) {
        mContext = context;
    }

    public ArrayList<PlaceTypeDetail> getPlaceTypeDetails() {
        if (mList != null && mList.size() != 0) {
            return mList;
        }

        return parseAndPopulate(mContext.getResources().openRawResource(R.raw.places));
    }

    /**
     * Parse XML and provide list of details about place types
     *
     * @param stream
     * @return
     */
    private ArrayList<PlaceTypeDetail> parseAndPopulate(InputStream stream) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            PlaceTypeDetail placeType = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(TAG_PLACE_TYPE)) {
                            // create a new instance of employee
                            placeType = new PlaceTypeDetail();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(TAG_PLACE_NAME)) {
                            placeType.setName(text);
                        } else if (tagname.equalsIgnoreCase(TAG_PLACE_DESCRIPTION)) {
                            placeType.setDescription(text);
                        } else if (tagname.equalsIgnoreCase(TAG_PLACE_INTENT)) {
                            placeType.setIntent(text);
                        } else if (tagname.equalsIgnoreCase(TAG_PLACE_ICON)) {
                            placeType.setIcon(text);
                        } else if (tagname.equalsIgnoreCase(TAG_PLACE_TYPE)) {
                            mList.add(placeType);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mList;
    }
}
