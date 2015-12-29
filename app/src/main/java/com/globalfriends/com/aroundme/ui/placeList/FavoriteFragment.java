package com.globalfriends.com.aroundme.ui.placeList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.provider.AroundMeContractProvider;

/**
 * Created by vishal on 11/14/2015.
 */
public class FavoriteFragment extends ListFragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent myData = getActivity().getIntent();
        Bundle info = myData.getExtras();
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.layout_places_item, null, new String[]{
                AroundMeContractProvider.PlacesColumns.PLACE_NAME,AroundMeContractProvider.PlacesColumns.FORMATTED_ADDRESS,AroundMeContractProvider.PlacesColumns.PHONE_NUMBER}
                , new int[]{R.id.place_name,R.id.vicinity, R.id.phone_number}, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, info, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return (Loader<Cursor>) new CursorLoader(getActivity(), AroundMeContractProvider.Places.CONTENT_URI,
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor((android.database.Cursor) cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
