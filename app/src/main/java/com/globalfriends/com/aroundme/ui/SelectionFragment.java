package com.globalfriends.com.aroundme.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.places.PlaceTypeDetail;
import com.globalfriends.com.aroundme.data.places.PlaceTypeParser;
import com.globalfriends.com.aroundme.logging.Logger;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSelectionFragmentSelection}
 * interface.
 */
public class SelectionFragment extends BaseFragment implements AbsListView.OnItemClickListener {
    private static final String TAG = "SelectionFragment";
    PlaceDetailsAdapter mAdapter = null;
    private OnSelectionFragmentSelection mListener;
    private AbsListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "OnCreate");
        mAdapter = new PlaceDetailsAdapter(getActivity(), new PlaceTypeParser(getActivity()).getPlaceTypeDetails());
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarUpdater.onNavigationEnabled(true);
        mToolbarUpdater.onSearchBarEnabled(true);
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        Logger.i(TAG, "ListItems=" + mAdapter.getCount());
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Logger.i(TAG, "onAttach");
        try {
            mListener = (OnSelectionFragmentSelection) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.i(TAG, "onItemClick");

        if (null != mListener) {
            PlaceTypeDetail details = (PlaceTypeDetail) parent.getItemAtPosition(position);
            Log.i(TAG, ">>>> " + details.toString());
            Bundle bundle = new Bundle();
            bundle.putString("PLACE_EXTRA", details.getExtra());
            mListener.OnSelectionFragmentSelection(bundle);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSelectionFragmentSelection {
        void OnSelectionFragmentSelection(Bundle bundle);
    }

    /**
     * Adapter Details..
     */
    class PlaceDetailsAdapter extends ArrayAdapter<PlaceTypeDetail> {
        public PlaceDetailsAdapter(Context context, ArrayList<PlaceTypeDetail> places) {
            super(context, 0, places);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            PlaceTypeDetail placeDetail = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.place_types_list_layout, parent, false);
            }

            ImageView placeImage = (ImageView) convertView.findViewById(R.id.place_icon);
            placeImage.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(placeDetail.getIcon(),
                    "drawable", getActivity().getPackageName())));

            // Lookup view for data population
            TextView placeName = (TextView) convertView.findViewById(R.id.place_name);
            placeName.setText(placeDetail.getName());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
