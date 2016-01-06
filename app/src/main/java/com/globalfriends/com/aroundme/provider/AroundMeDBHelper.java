package com.globalfriends.com.aroundme.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by swapna on 12/1/15.
 */
public class AroundMeDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public AroundMeDBHelper(Context context) {
        super(context, AroundMeDBSchema.DATABASE_NAME, null, VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createDB(SQLiteDatabase db) {

        db.execSQL(AroundMeDBSchema.CREATE_PLACES_TABLE);
        db.execSQL(AroundMeDBSchema.CREATE_RECENT_PLACES_TABLE);
        db.execSQL(AroundMeDBSchema.TRIGGER_DELETE_RECENT);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    public SQLiteDatabase getWritableDB(Context context) {
        return getWritableDatabase();
    }

    public SQLiteDatabase getReadableDB(Context context) {
        return getReadableDatabase();
    }
}
