package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.places.Places;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.TransactionManager;

/**
 *
 */
public class PlaceDetailsFragment extends Fragment {
    private static final String TAG = "PlaceDetailsFragment";
    private ProgressDialog mProgress;
    private OnPlaceDetailsFragmentInteractionListener mListener;
    private ResultResponse mResultListener = new ResultResponse();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setMessage(getResources().getString(R.string.please_wait_progress));
        mProgress.isIndeterminate();
        mProgress.show();

        TransactionManager.getInstance().addResultCallback(mResultListener);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Places place = bundle.getParcelable("PLACE");
            TransactionManager.getInstance().findPlaceDetails(place.getPlaceId(), null);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.place_detail_layout, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaceDetailsFragmentInteractionListener) {
            mListener = (OnPlaceDetailsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlaceDetailsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        TransactionManager.getInstance().removeResultCallback(mResultListener);
        mListener = null;
        super.onDetach();
    }

    public interface OnPlaceDetailsFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    class ResultResponse extends TransactionManager.Result {
        @Override
        public void onGetPlaceDetails(IPlaceDetails response, String placeTag) {
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
            Logger.i(TAG, "Response received");
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            super.onError(errorMsg, tag);
        }
    }
}
