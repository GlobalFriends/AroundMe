package com.globalfriends.com.aroundme.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashSet;

/**
 * Created by swapna on 12/1/15.
 */
public abstract class AroundMeContractProvider {
    public static final String TAG = "AroundMeContractProvider";
    // The authority of the AroundMe provider.
    public static final String AUTHORITY = "com.globalfriends.aroundme.provider";
    public static final String PARAMETER_LIMIT = "limit";
    // The content URI for the top-level AroundMe authority.
    protected static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // URI for the table
    public static Uri mBaseUri;
    // Record identifier
    public long mId = -1;

    /**
     * Construct the Uri to query maximum given number of records
     *
     * @param uri   : Uri to query
     * @param limit : Maximum number of records to query
     * @return
     */
    public static Uri uriWithLimit(Uri uri, int limit) {
        return uri.buildUpon()
                .appendQueryParameter(PARAMETER_LIMIT, Integer.toString(limit))
                .build();

    }

    /**
     * Utility Method to map the fields based on Class instance
     *
     * @param cursor : Database cursor to table
     * @param klass  : Class name to be mapped
     * @param <T>    : Templatized construct
     * @return T : Invokes the restore method of the provided klass
     */
    static public <T extends AroundMeContractProvider> T getContent(Cursor cursor, Class<T> klass) {
        try {
            T content = klass.newInstance();
            content.mId = cursor.getLong(0);
            content.restore(cursor);
            return content;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method needs to be overriden to return the fields of table based on database cursor
     *
     * @param cursor : Cursor to table for which query was initiated
     */
    public abstract void restore(Cursor cursor);

    /**
     * Method needs to be overridern to map each class member variables to ContentValues class
     * for Sqlite database query to be executed
     *
     * @return ContentValues:
     */
    public abstract ContentValues toContentValues();

    public Uri save(Context context) {
        if (context != null) {
            Uri res = context.getContentResolver().insert(mBaseUri, toContentValues());
            if (res == null)
                return null;
            mId = Long.parseLong(res.getPathSegments().get(1));
            return res;
        } else {
            return null;
        }
    }

    public void delete(Context context, String where, String[] selectionArgs) {
        if (context != null) {
            context.getContentResolver().delete(mBaseUri, where, selectionArgs);
        }
    }

    public interface PlacesColumns {
        String _ID = "_id";
        String OPEN_NOW = "open_now";
        String RATING = "rating";
        String GEOMETRY_LOCATION_LATITUDE = "latitude";
        String GEOMETRY_LOCATTION_LONGITUDE = "longitude";
        String PLACES_ID = "places_id";
        String PHONE_NUMBER = "phone_number";
        String PHOTO_REFERENCE = "photo_reference";
        String FORMATTED_ADDRESS = "formatted_address";
        String PLACE_NAME = "place_name";
    }

    public interface RecentPlacesColumns {
        String _ID = "_id";
        String OPEN_NOW = "open_now";
        String RATING = "rating";
        String GEOMETRY_LOCATION_LATITUDE = "latitude";
        String GEOMETRY_LOCATTION_LONGITUDE = "longitude";
        String PLACES_ID = "places_id";
        String PHONE_NUMBER = "phone_number";
        String PHOTO_REFERENCE = "photo_reference";
        String FORMATTED_ADDRESS = "formatted_address";
        String PLACE_NAME = "place_name";
        String TIMESTAMP = "timestamp";
    }

    //Image Reference Table
    public interface PhotoReferenceColumns {
        String _ID = "_id";
        String URL = "url";
        String PHOTO_REF = "ref";
        String PHOTO_URL = "photo_url";
        String PHOTO_HEIGHT = "height";
        String PHOTO_WIDTH = "width";
        String PHOTO_PLACE_ID = "photo_place_id";
    }

    public static final class Places extends AroundMeContractProvider implements PlacesColumns {
        public static final String TABLENAME = "places";
        public static final String PATH = "places";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AroundMeContractProvider.CONTENT_URI, PATH);

        public static final int CONTENT_ID_COL = 0;
        public static final int CONTENT_OPEN_NOW_COL = 1;
        public static final int CONTENT_RATING_COL = 2;
        public static final int CONTENT_GEMOTERY_LOCATION_LATITUDE = 3;
        public static final int CONTENT_GEMOTERY_LOCATION_LONGITUDE = 4;
        public static final int CONTENT_PLACES_ID_COL = 5;
        public static final int CONTENT_PHONE_NUMBER_COL = 6;
        public static final int CONTENT_PHOTO_REFERENCE_COL = 7;
        public static final int CONTENT_FORMATTED_ADDRESS_COL = 8;
        public static final int CONTENT_PLACE_NAME_COL = 9;


        private boolean mIsOpenNow;
        private double mRating;
        private double mLatitude;
        private double mLongitude;
        private String mPlaceId;
        private String mPhoneNumber;
        private String mPhotoRefrence;
        private String mAddress;
        private String mName;

        public Places(boolean isOpenNow, double rating, double latitude,
                      double longitude, String placeId, String phoneNumber,
                      String photoRefernce, String address, String name) {
            mBaseUri = CONTENT_URI;
            mIsOpenNow = isOpenNow;
            mRating = rating;
            mLatitude = latitude;
            mLongitude = longitude;
            mPlaceId = placeId;
            mPhoneNumber = phoneNumber;
            mPhotoRefrence = photoRefernce;
            mAddress = address;
            mName = name;
        }

        public Places() {
            mBaseUri = CONTENT_URI;
        }

        public static int insertMultiple(Context context, HashSet<Places> placesSet) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            ContentValues[] values = new ContentValues[placesSet.size()];
            int count = 0;
            for (Places place : placesSet) {
                values[count] = place.toContentValues();
                count++;
            }
            return resolver.bulkInsert(CONTENT_URI, values);
        }

        public static int deleteAllRecords(Context context) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            return resolver.delete(CONTENT_URI, null, null);
        }

        public static int deleteAllRecordsWhere(Context context, String where, String[] selectionArgs) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            return resolver.delete(CONTENT_URI, where, selectionArgs);
        }

        private static Uri updateExisting(Context context, ContentValues values) {
            if (context == null) {
                return null;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            Uri uri = null;
            Cursor c = resolver.query(CONTENT_URI, null, null, null, null);
            int id = c.getInt(CONTENT_ID_COL);
            uri = ContentUris.withAppendedId(CONTENT_URI, id);
            resolver.update(uri, values, null, null);
            resolver.notifyChange(uri, null);
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return uri;
        }

        public static boolean exist(Context context, String placeId) {
            String[] columns = {PlacesColumns.PLACES_ID};
            String selection = PlacesColumns.PLACES_ID + " =?";
            String[] selectionArgs = {placeId};
            String limit = "1";
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return false;
            }
            Uri uri = null;
            Cursor c = resolver.query(CONTENT_URI, null, selection, selectionArgs, null);
            boolean exists = (c.getCount() > 0);
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return exists;
        }

        @Override
        public void restore(Cursor cursor) {
            mBaseUri = Places.CONTENT_URI;
            mIsOpenNow = cursor.getInt(CONTENT_OPEN_NOW_COL) == 1;
            mRating = cursor.getDouble(CONTENT_RATING_COL);
            mLatitude = cursor.getDouble(CONTENT_GEMOTERY_LOCATION_LATITUDE);
            mLongitude = cursor.getDouble(CONTENT_GEMOTERY_LOCATION_LONGITUDE);
            mPlaceId = cursor.getString(CONTENT_PLACES_ID_COL);
            mPhoneNumber = cursor.getString(CONTENT_PHONE_NUMBER_COL);
            mPhotoRefrence = cursor.getString(CONTENT_PHOTO_REFERENCE_COL);
            mAddress = cursor.getString(CONTENT_FORMATTED_ADDRESS_COL);
            mName = cursor.getString(CONTENT_PLACE_NAME_COL);
        }

        @Override
        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            values.put(PlacesColumns.OPEN_NOW, mIsOpenNow);
            values.put(PlacesColumns.RATING, mRating);
            values.put(PlacesColumns.GEOMETRY_LOCATION_LATITUDE, mLatitude);
            values.put(PlacesColumns.GEOMETRY_LOCATTION_LONGITUDE, mLongitude);
            values.put(PlacesColumns.PLACES_ID, mPlaceId);
            values.put(PlacesColumns.PHONE_NUMBER, mPhoneNumber);
            values.put(PlacesColumns.PHONE_NUMBER, mPhoneNumber);
            values.put(PlacesColumns.PHOTO_REFERENCE, mPhotoRefrence);
            values.put(PlacesColumns.FORMATTED_ADDRESS, mAddress);
            values.put(PlacesColumns.PLACE_NAME, mName);
            return values;
        }
    }

    public static final class RecentPlaces extends AroundMeContractProvider implements RecentPlacesColumns {
        public static final String TABLENAME = "recentplaces";
        public static final String PATH = "recentplaces";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AroundMeContractProvider.CONTENT_URI, PATH);

        public static final int CONTENT_ID_COL = 0;
        public static final int CONTENT_OPEN_NOW_COL = 1;
        public static final int CONTENT_RATING_COL = 2;
        public static final int CONTENT_GEMOTERY_LOCATION_LATITUDE = 3;
        public static final int CONTENT_GEMOTERY_LOCATION_LONGITUDE = 4;
        public static final int CONTENT_PLACES_ID_COL = 5;
        public static final int CONTENT_PHONE_NUMBER_COL = 6;
        public static final int CONTENT_PHOTO_REFERENCE_COL = 7;
        public static final int CONTENT_FORMATTED_ADDRESS_COL = 8;
        public static final int CONTENT_PLACE_NAME_COL = 9;
        public static final int CONTENT_TIMESTAMP_COL = 10;


        private boolean mIsOpenNow;
        private double mRating;
        private double mLatitude;
        private double mLongitude;
        private String mPlaceId;
        private String mPhoneNumber;
        private String mPhotoRefrence;
        private String mAddress;
        private String mName;
        private long mTimeStamp;

        public RecentPlaces(boolean isOpenNow, double rating, double latitude,
                            double longitude, String placeId, String phoneNumber,
                            String photoRefernce, String address, String name) {
            mBaseUri = CONTENT_URI;
            mIsOpenNow = isOpenNow;
            mRating = rating;
            mLatitude = latitude;
            mLongitude = longitude;
            mPlaceId = placeId;
            mPhoneNumber = phoneNumber;
            mPhotoRefrence = photoRefernce;
            mAddress = address;
            mName = name;
            mTimeStamp = System.currentTimeMillis();
        }

        public static int insertMultiple(Context context, HashSet<RecentPlaces> placesSet) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            ContentValues[] values = new ContentValues[placesSet.size()];
            int count = 0;
            for (RecentPlaces place : placesSet) {
                values[count] = place.toContentValues();
                count++;
            }
            return resolver.bulkInsert(CONTENT_URI, values);
        }

        public static int deleteAllRecords(Context context) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            return resolver.delete(CONTENT_URI, null, null);
        }

        public static int deleteAllRecordsWhere(Context context, String where, String[] selectionArgs) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            return resolver.delete(CONTENT_URI, where, selectionArgs);
        }

        private static Uri updateExisting(Context context, ContentValues values) {
            if (context == null) {
                return null;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            Uri uri = null;
            Cursor c = resolver.query(CONTENT_URI, null, null, null, null);
            int id = c.getInt(CONTENT_ID_COL);
            uri = ContentUris.withAppendedId(CONTENT_URI, id);
            resolver.update(uri, values, null, null);
            resolver.notifyChange(uri, null);
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return uri;
        }

        public static boolean exist(Context context, String placeId) {
            String[] columns = {RecentPlacesColumns.PLACES_ID};
            String selection = RecentPlaces.PLACES_ID + " =?";
            String[] selectionArgs = {placeId};
            String limit = "1";
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return false;
            }
            Uri uri = null;
            Cursor c = resolver.query(CONTENT_URI, null, selection, selectionArgs, null);
            boolean exists = (c.getCount() > 0);
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return exists;
        }

        @Override
        public void restore(Cursor cursor) {
            mBaseUri = RecentPlaces.CONTENT_URI;
            mIsOpenNow = cursor.getInt(CONTENT_OPEN_NOW_COL) == 1;
            mRating = cursor.getDouble(CONTENT_RATING_COL);
            mLatitude = cursor.getDouble(CONTENT_GEMOTERY_LOCATION_LATITUDE);
            mLongitude = cursor.getDouble(CONTENT_GEMOTERY_LOCATION_LONGITUDE);
            mPlaceId = cursor.getString(CONTENT_PLACES_ID_COL);
            mPhoneNumber = cursor.getString(CONTENT_PHONE_NUMBER_COL);
            mPhotoRefrence = cursor.getString(CONTENT_PHOTO_REFERENCE_COL);
            mAddress = cursor.getString(CONTENT_FORMATTED_ADDRESS_COL);
            mName = cursor.getString(CONTENT_PLACE_NAME_COL);
            mTimeStamp = cursor.getLong(CONTENT_TIMESTAMP_COL);
        }

        @Override
        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            values.put(RecentPlacesColumns.OPEN_NOW, mIsOpenNow);
            values.put(RecentPlacesColumns.RATING, mRating);
            values.put(RecentPlacesColumns.GEOMETRY_LOCATION_LATITUDE, mLatitude);
            values.put(RecentPlacesColumns.GEOMETRY_LOCATTION_LONGITUDE, mLongitude);
            values.put(RecentPlacesColumns.PLACES_ID, mPlaceId);
            values.put(RecentPlacesColumns.PHONE_NUMBER, mPhoneNumber);
            values.put(RecentPlacesColumns.PHONE_NUMBER, mPhoneNumber);
            values.put(RecentPlacesColumns.PHOTO_REFERENCE, mPhotoRefrence);
            values.put(RecentPlacesColumns.FORMATTED_ADDRESS, mAddress);
            values.put(RecentPlacesColumns.PLACE_NAME, mName);
            values.put(RecentPlacesColumns.TIMESTAMP, mTimeStamp);
            return values;
        }
    }

    public static final class PhotoRefernce extends AroundMeContractProvider implements PhotoReferenceColumns {
        public static final String TABLENAME = "photometadata";
        public static final String PATH = "photometadata";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AroundMeContractProvider.CONTENT_URI, PATH);

        public static final int CONTENT_ID_COL = 0;
        public static final int CONTENT_URL = 1;
        public static final int CONTENT_PHOTO_REF_COL = 2;
        public static final int CONTENT_PHOTO_URL_COL = 3;
        public static final int CONTENT_PHOTO_HEIGHT_COL = 4;
        public static final int CONTENT_PHOTO_WIDTH_COL = 5;
        public static final int CONTENT_PHOTO_PLACE_ID = 6;


        private String mPlaceId;
        private String mUrl;
        private String mPhotoRefUrl;
        private String mPhotoUrl;
        private int mHeight;
        private int mWidth;

        public PhotoRefernce(String placeId, String url, String photoUrl, String photoRef, int width, int height) {
            mBaseUri = CONTENT_URI;
            mPlaceId = placeId;
            mUrl = url;
            mPhotoRefUrl = photoRef;
            mPhotoUrl = photoUrl;
            mHeight = height;
            mWidth = width;
        }

        public static int insertMultiple(Context context, HashSet<Places> placesSet) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            ContentValues[] values = new ContentValues[placesSet.size()];
            int count = 0;
            for (Places place : placesSet) {
                values[count] = place.toContentValues();
                count++;
            }
            return resolver.bulkInsert(CONTENT_URI, values);
        }

        public static int deleteAllRecords(Context context) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            return resolver.delete(CONTENT_URI, null, null);
        }

        public static int deleteAllRecordsWhere(Context context, String where, String[] selectionArgs) {
            if (context == null) {
                return -1;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return -1;
            }
            return resolver.delete(CONTENT_URI, where, selectionArgs);
        }

        private static Uri updateExisting(Context context, ContentValues values) {
            if (context == null) {
                return null;
            }
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            Uri uri = null;
            Cursor c = resolver.query(CONTENT_URI, null, null, null, null);
            int id = c.getInt(CONTENT_ID_COL);
            uri = ContentUris.withAppendedId(CONTENT_URI, id);
            resolver.update(uri, values, null, null);
            resolver.notifyChange(uri, null);
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return uri;
        }

        public static boolean exist(Context context, String placeId) {
            String[] columns = {PlacesColumns.PLACES_ID};
            String selection = PlacesColumns.PLACES_ID + " =?";
            String[] selectionArgs = {placeId};
            String limit = "1";
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return false;
            }
            Uri uri = null;
            Cursor c = resolver.query(CONTENT_URI, null, selection, selectionArgs, null);
            boolean exists = (c.getCount() > 0);
            if (c != null && !c.isClosed()) {
                c.close();
            }
            return exists;
        }

        @Override
        public void restore(Cursor cursor) {
            mBaseUri = Places.CONTENT_URI;

            mPlaceId = cursor.getString(CONTENT_PHOTO_PLACE_ID);
            mUrl = cursor.getString(CONTENT_PHOTO_URL_COL);
            mPhotoRefUrl = cursor.getString(CONTENT_PHOTO_REF_COL);
            mPhotoUrl = cursor.getString(CONTENT_PHOTO_URL_COL);
            mWidth = cursor.getInt(CONTENT_PHOTO_WIDTH_COL);
            mHeight = cursor.getInt(CONTENT_PHOTO_HEIGHT_COL);
        }

        @Override
        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            values.put(PhotoReferenceColumns.URL, mUrl);
            values.put(PhotoReferenceColumns.PHOTO_REF, mPhotoRefUrl);
            values.put(PhotoReferenceColumns.PHOTO_URL, mPhotoUrl);
            values.put(PhotoReferenceColumns.PHOTO_PLACE_ID, mPlaceId);
            values.put(PhotoReferenceColumns.PHOTO_HEIGHT, mHeight);
            values.put(PhotoReferenceColumns.PHOTO_WIDTH, mWidth);
            return values;
        }
    }

}
