package com.globalfriends.com.aroundme.ui.placeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.TransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListFragment extends ListFragment {
    private static final String TAG = "PlacesListFragment";
    private List<PlaceInfo> mPlaces = new ArrayList<>();
    private OnPlaceListFragmentSelection mListener;
    private PlacesListAdapter mAdapter;
    private ProgressDialog mProgress;
    private ResultCallback mCallBack = new ResultCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setMessage(getResources().getString(R.string.please_wait_progress));
        mProgress.isIndeterminate();
        mProgress.show();
        TransactionManager.getInstance().addResultCallback(mCallBack);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new PlacesListAdapter(getActivity(), mPlaces);
        setListAdapter(mAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TransactionManager.getInstance().findByNearBy(getArguments().getString("PLACE_EXTRA"));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.search_results);
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        if (null != mListener) {
            PlaceInfo details = (PlaceInfo) parent.getItemAtPosition(position);
            Log.i(TAG, ">>>> " + details.toString());
            mListener.OnPlaceListFragmentSelection(details);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPlaceListFragmentSelection) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
    }

    /**
     * Handle selection from this fragment to main acitivty
     */
    public interface OnPlaceListFragmentSelection {
        /**
         * @param place
         */
        void OnPlaceListFragmentSelection(final PlaceInfo place);

        /**
         * @param tag
         */
        void handleFragmentSuicidal(final String tag);
    }

    class ResultCallback extends TransactionManager.Result {
        @Override
        public void onPlacesList(List<PlaceInfo> placeList) {
            Log.i(TAG, "onPlacesList ....");
            mAdapter.swapItem(placeList);
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            if (!TextUtils.isEmpty(tag) && !tag.equalsIgnoreCase(getActivity().getResources().getString(R.string.google_places_tag))) {
                Log.e(TAG, "This is not a google response..ust ignore it");
                return;
            }

            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.error_dialog_title))
                    .setMessage(errorMsg)
                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.handleFragmentSuicidal(TAG);
                        }
                    })
                    .show();
        }
    }
}
