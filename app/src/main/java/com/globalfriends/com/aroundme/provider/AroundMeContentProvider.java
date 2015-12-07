package com.globalfriends.com.aroundme.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by swapna on 12/1/15.
 */
public class AroundMeContentProvider extends ContentProvider {
    // helper constants for use with the UriMatcher
    private static final int PLACES_BASE = 0;
    private static final int PLACES_LIST = PLACES_BASE;
    private static final int PLACES_ID = PLACES_BASE + 1;

    private static final int BASE_SHIFT = 12;
    private static final UriMatcher URI_MATCHER;
    private static final String sTABLE_NAME[] = {
            AroundMeContractProvider.Places.TABLENAME
    };

    private AroundMeDBHelper mDBHelper;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<>();

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AroundMeContractProvider.AUTHORITY,
                AroundMeContractProvider.Places.PATH + "/#", PLACES_ID);
        URI_MATCHER.addURI(AroundMeContractProvider.AUTHORITY,
                AroundMeContractProvider.Places.PATH, PLACES_LIST);
    }

    @Override
    public boolean onCreate() {
        getWritableDB();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int match = findMatch(uri);
        String tableName = getTableName(match);
        queryBuilder.setTables(tableName);
        switch (match) {
            case PLACES_LIST:
                queryBuilder.setProjectionMap(null);
                break;
            case PLACES_ID:
                queryBuilder.appendWhere("_id" + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for query: " + uri);
        }
        Cursor cursor = queryBuilder.query(getReabadbleDB(), projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long _id = -1;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int match = findMatch(uri);
        String tableName = getTableName(match);
        queryBuilder.setTables(tableName);
        SQLiteDatabase db = getWritableDB();
        switch (match) {
            case PLACES_LIST:
                _id = db.insert(tableName, null, values);
                break;
            case PLACES_ID:
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for insertion: " + uri);
        }
        return getUriForId(_id, uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int match = findMatch(uri);
        String tableName = getTableName(match);
        queryBuilder.setTables(tableName);
        SQLiteDatabase db = getWritableDB();
        int count = 0;
        switch (match) {
            case PLACES_LIST:
                count = db.delete(tableName, selection, selectionArgs);
                break;
            case PLACES_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(tableName, whereWithId(id, selection),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for insertion: " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int match = findMatch(uri);
        String tableName = getTableName(match);
        queryBuilder.setTables(tableName);
        SQLiteDatabase db = getWritableDB();
        int count = 0;
        switch (match) {
            case PLACES_LIST:
                count = db.update(tableName, values, selection, selectionArgs);
                break;
            case PLACES_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(tableName, values, whereWithId(id, selection),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for insertion: " + uri);
        }
        return count;
    }

    public SQLiteDatabase getWritableDB() {
        if (mDBHelper == null) {
            mDBHelper = new AroundMeDBHelper(getContext());
        }
        return mDBHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReabadbleDB() {
        if (mDBHelper == null) {
            mDBHelper = new AroundMeDBHelper(getContext());
        }
        return mDBHelper.getReadableDB(getContext());
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int match = findMatch(uri);
        String tableName = getTableName(match);
        queryBuilder.setTables(tableName);
        SQLiteDatabase db = getWritableDB();
        int count = 0;
        switch (match) {
            case PLACES_LIST:
                try {
                    db.beginTransaction();
                    //ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                    for (ContentValues cv : values) {
                        long newID = db.insert(tableName, null, cv);
                        if (newID <= 0) {
                            throw new SQLException("Error to add: " + uri);
                        }
                    }
                    db.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                    count = values.length;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                break;
            case PLACES_ID:
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for insertion: " + uri);
        }
        return count;
    }

    /**
     * @param operations Batch operation list to be perfomed
     * @return ContentProviderResult Array
     * @throws OperationApplicationException
     */
    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = getWritableDB();
        ContentProviderResult[] retResult = null;
        mIsInBatchMode.set(true);
        // the next line works because SQLiteDatabase
        // uses a thread local SQLiteSession object for
        // all manipulations
        db.beginTransaction();
        try {
            retResult = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(AroundMeContractProvider.CONTENT_URI, null);
            return retResult;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mIsInBatchMode.remove();
            db.endTransaction();
        }
        return retResult;
    }

    protected int findMatch(Uri uri) throws IllegalArgumentException {
        int match = URI_MATCHER.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        return match;
    }

    private String getTableName(int match) {
        int table = match >> BASE_SHIFT;
        return sTABLE_NAME[table];
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    private String whereWithId(String id, String selection) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("_id=");
        sb.append(id);
        if (selection != null) {
            sb.append(" AND (");
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes and return itemUri:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

}
