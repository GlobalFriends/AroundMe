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
            + AroundMeContractProvider.PlacesColumns.FORMATTED_ADDRESS + " TEXT "
            + " )";

}
