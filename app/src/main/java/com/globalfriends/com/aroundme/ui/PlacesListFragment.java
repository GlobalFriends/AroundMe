package com.globalfriends.com.aroundme.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalfriends.com.aroundme.data.places.Places;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListFragment extends ListFragment {
    Places[] mPlaces;

    public PlacesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        Parcelable[] parcelables = bundle.getParcelableArray("PLACES_LIST");

        mPlaces = new Places[parcelables.length];
        for (int i = 0; i < parcelables.length; i++) {
            mPlaces[i] = (Places) parcelables[i];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecentFragment.PlacesListAdapter placesListAdapter = new RecentFragment.PlacesListAdapter(getActivity(), mPlaces);
        setListAdapter(placesListAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        getListView().setDivider(null);
    }
}
