package com.globalfriends.com.aroundme.ui.placeList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.provider.AroundMeContractProvider;
import com.globalfriends.com.aroundme.ui.ToolbarUpdateListener;

/**
 * Created by vishal on 11/14/2015.
 */
public class RecentFragment extends ListFragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter mAdapter;
    private ToolbarUpdateListener mToolbarUpdater;
    private OnRecentFragmentInteractionListener mListener;

    public interface OnRecentFragmentInteractionListener {
        /**
         * Launch Place Details
         *
         * @param placeId
         */
        void onRecentViewClicked(String placeId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent myData = getActivity().getIntent();
        Bundle info = myData.getExtras();
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.layout_fav_places_item, null, new String[]{
                AroundMeContractProvider.RecentPlacesColumns.PLACE_NAME, AroundMeContractProvider.RecentPlacesColumns.FORMATTED_ADDRESS, AroundMeContractProvider.PlacesColumns.PHONE_NUMBER,
                AroundMeContractProvider.RecentPlacesColumns.PLACES_ID}
                , new int[]{R.id.place_name, R.id.vicinity, R.id.phone_number, R.id.place_id}, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, info, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = mAdapter.getCursor();
        String placeId = cursor.getString(cursor.getColumnIndex(AroundMeContractProvider.PlacesColumns.PLACES_ID));
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        mListener.onRecentViewClicked(placeId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mToolbarUpdater = (ToolbarUpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
        mListener = (OnRecentFragmentInteractionListener) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarUpdater.onNavigationEnabled(false);
        mToolbarUpdater.onSearchBarEnabled(false);
        getActivity().setTitle(R.string.recent_title);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return (Loader<Cursor>) new CursorLoader(getActivity(), AroundMeContractProvider.RecentPlaces.CONTENT_URI,
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
