package com.globalfriends.com.aroundme.provider;

/**
 * Created by swapna on 12/1/15.
 */
interface AroundMeDBSchema {
    String DATABASE_NAME= "aroundmedb";

    public final static String CREATE_PLACES_TABLE = "CREATE TABLE "
            + AroundMeContractProvider.Places.TABLENAME
            + " ( "
            + AroundMeContractProvider.PlacesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + AroundMeContractProvider.PlacesColumns.OPEN_NOW + " INTEGER DEFAULT TRUE , "
            + AroundMeContractProvider.PlacesColumns.RATING + " REAL ,"
            + AroundMeContractProvider.PlacesColumns.GEOMETRY_LOCATION_LATITUDE + " REAL ,"
            + AroundMeContractProvider.PlacesColumns.GEOMETRY_LOCATTION_LONGITUDE + " REAL ,"
            + AroundMeContractProvider.PlacesColumns.PLACES_ID + " TEXT NOT NULL ,"
            + AroundMeContractProvider.PlacesColumns.PHONE_NUMBER + " TEXT ,"
            + AroundMeContractProvider.PlacesColumns.PHOTO_REFERENCE + " TEXT ,"
            + AroundMeContractProvider.PlacesColumns.PLACE_NAME + " TEXT ,"
            + AroundMeContractProvider.PlacesColumns.FORMATTED_ADDRESS + " TEXT "
            + " )";

        public final static String CREATE_RECENT_PLACES_TABLE = "CREATE TABLE "
                + AroundMeContractProvider.RecentPlaces.TABLENAME
                + " ( "
                + AroundMeContractProvider.RecentPlacesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + AroundMeContractProvider.RecentPlacesColumns.OPEN_NOW + " INTEGER DEFAULT TRUE , "
                + AroundMeContractProvider.RecentPlacesColumns.RATING + " REAL ,"
                + AroundMeContractProvider.RecentPlacesColumns.GEOMETRY_LOCATION_LATITUDE + " REAL ,"
                + AroundMeContractProvider.RecentPlacesColumns.GEOMETRY_LOCATTION_LONGITUDE + " REAL ,"
                + AroundMeContractProvider.RecentPlacesColumns.PLACES_ID + " TEXT NOT NULL ,"
                + AroundMeContractProvider.RecentPlacesColumns.PHONE_NUMBER + " TEXT ,"
                + AroundMeContractProvider.RecentPlacesColumns.PHOTO_REFERENCE + " TEXT ,"
                + AroundMeContractProvider.RecentPlacesColumns.PLACE_NAME + " TEXT ,"
                + AroundMeContractProvider.RecentPlacesColumns.FORMATTED_ADDRESS + " TEXT "
                + " )";
}
