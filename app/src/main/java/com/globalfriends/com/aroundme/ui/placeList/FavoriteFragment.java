package com.globalfriends.com.aroundme.ui.placeList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class FavoriteFragment extends ListFragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter mAdapter;
    private ToolbarUpdateListener mToolbarUpdater;
    private OnFavoriteFragmentInteractionListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_recent_layout, container, false);
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
        mListener = (OnFavoriteFragmentInteractionListener) context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent data = getActivity().getIntent();
        Bundle info = data.getExtras();
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.layout_fav_places_item, null, new String[]{
                AroundMeContractProvider.PlacesColumns.PLACE_NAME, AroundMeContractProvider.PlacesColumns.FORMATTED_ADDRESS, AroundMeContractProvider.PlacesColumns.PHONE_NUMBER,
                AroundMeContractProvider.PlacesColumns.PLACES_ID}
                , new int[]{R.id.place_name, R.id.vicinity, R.id.phone_number, R.id.place_id}, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, info, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = mAdapter.getCursor();
        String placeId = cursor.getString(cursor.getColumnIndex(AroundMeContractProvider.PlacesColumns.PLACES_ID));
        mListener.onFavoriteViewClicked(placeId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        mToolbarUpdater.onNavigationEnabled(false);
        mToolbarUpdater.onSearchBarEnabled(false);
        getActivity().setTitle(R.string.favorite_title);
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

    public interface OnFavoriteFragmentInteractionListener {
        /**
         * Launch Place Details
         *
         * @param placeId
         */
        void onFavoriteViewClicked(String placeId);

    }
}
