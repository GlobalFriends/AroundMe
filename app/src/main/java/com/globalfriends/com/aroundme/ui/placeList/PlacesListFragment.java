package com.globalfriends.com.aroundme.ui.placeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.ui.ToolbarUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "PlacesListFragment";
    private List<PlaceInfo> mPlaces = new ArrayList<>();
    private OnPlaceListFragmentSelection mListener;
    private ToolbarUpdateListener mToolbarUpdater;
    private PlacesListAdapter mAdapter;
    private ProgressDialog mProgress;
    private SwipeRefreshLayout mSwipeRefresh;
    private ResultCallback mCallBack = new ResultCallback();
    private String mNextPageToken;

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        TransactionManager.getInstance().removeResultCallback(mCallBack);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register callback to make sure everything will be received
        TransactionManager.getInstance().addResultCallback(mCallBack);

        // Create progress and initiate transaction
        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setMessage(getResources().getString(R.string.please_wait_progress));
        mProgress.isIndeterminate();
        mProgress.show();
        String nearBy = getArguments().getString("PLACE_EXTRA");

        if (!TextUtils.isEmpty(nearBy)) {
            TransactionManager.getInstance().findByNearBy(nearBy);
        } else {
            TransactionManager.getInstance().findBySearch(getArguments().getString("TEXT_EXTRA"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new PlacesListAdapter(getActivity(), mPlaces);
        setListAdapter(mAdapter);
        return inflater.inflate(R.layout.fragment_places_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mSwipeRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getListView().setDivider(null);
        mToolbarUpdater.onNavigationEnabled(false);
        mToolbarUpdater.onSearchBarEnabled(false);
        mToolbarUpdater.settingsOptionUpdate(true);
        getActivity().setTitle(R.string.search_results);
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
            mToolbarUpdater = (ToolbarUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh");
        if (mNextPageToken != null) {
            TransactionManager.getInstance().findByNearByByPageToken(mNextPageToken);
            return;
        }

        mSwipeRefresh.setRefreshing(false);
        Toast.makeText(getActivity(), R.string.no_more_results, Toast.LENGTH_SHORT).show();
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

    /**
     *
     */
    class ResultCallback extends TransactionManager.Result {
        @Override
        public void onPlacesList(final String nextPageToken, List<PlaceInfo> placeList) {
            Log.i(TAG, "onPlacesList isVisible" + isVisible());
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }

            if (mSwipeRefresh != null) {
                mSwipeRefresh.setRefreshing(false);
            }

            boolean showUpdate = false;
            if (mNextPageToken != null) {
                showUpdate = true;
            }
            mNextPageToken = nextPageToken;
            if (placeList == null || placeList.size() == 0) {
                Toast.makeText(getActivity(), R.string.no_results, Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(TAG, "onPlacesList isAdded" + isAdded());
            if (mAdapter != null) {
                mAdapter.swapItem(placeList);
                if (showUpdate) {
                    Toast.makeText(getActivity(), R.string.updated, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            if (mSwipeRefresh != null) {
                mSwipeRefresh.setRefreshing(false);
            }

            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }

            mNextPageToken = null; // Reset to recover

            if (!isAdded()) {
                Log.e(TAG, "Fragment is currently not isAdded");
                return;
            }

            if (!getString(R.string.google_places_tag).equalsIgnoreCase(tag)) {
                Log.i(TAG, "Not a google response. Google=" + tag);
                return;
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.error_dialog_title))
                    .setMessage(errorMsg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().onBackPressed();
                        }
                    }).show();
        }
    }
}
