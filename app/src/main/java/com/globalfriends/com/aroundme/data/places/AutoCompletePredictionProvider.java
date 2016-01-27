package com.globalfriends.com.aroundme.data.places;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.globalfriends.com.aroundme.protocol.TransactionManager;

import java.util.List;

public class AutoCompletePredictionProvider extends ContentProvider {
    private static final String[] SEARCH_SUGGEST_COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
    };
    public static boolean mPlaceSearchEnabled = false;
    public static boolean mQuerySearchEnabled = false;
    private List<AutoCompletePrediction> mPredictions;
    private ResultCallback mResultCallback = new ResultCallback();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!mPlaceSearchEnabled && !mQuerySearchEnabled) {
            return null;
        }

        String input = uri.getLastPathSegment();

        if (TextUtils.isEmpty(input) || input.equals(SearchManager.SUGGEST_URI_PATH_QUERY)) {
            return null;
        }

        input = input.replace(" ", "%20");

        MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);

        TransactionManager.getInstance().addResultCallback(mResultCallback);
        if (mPlaceSearchEnabled) {
            TransactionManager.getInstance().placeAutoComplete(input);
        } else {
            TransactionManager.getInstance().queryAutoComplete(input);
        }

        try {
            synchronized (mResultCallback) {
                mResultCallback.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = 0;
        for (AutoCompletePrediction prediction : mPredictions) {
            String placeId = prediction.getPlaceId();
            String intentData = (placeId == null ? "desc:" + prediction.getDescription() : "id:" + placeId);
            cursor.addRow(new String[]{Integer.toString(i), prediction.getDescription(), intentData});
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    class ResultCallback extends TransactionManager.Result {
        @Override
        public void onPlaceAutoComplete(List<AutoCompletePrediction> predictions) {
            mPredictions = predictions;

            synchronized (mResultCallback) {
                mResultCallback.notify();
            }
        }

        @Override
        public void onQueryAutoComplete(List<AutoCompletePrediction> predictions) {
            mPredictions = predictions;

            synchronized (mResultCallback) {
                mResultCallback.notify();
            }
        }

        @Override
        public void onError(String errorMsg, String tag) {
            mPredictions = null;

            synchronized (mResultCallback) {
                mResultCallback.notify();
            }
        }
    }
}
