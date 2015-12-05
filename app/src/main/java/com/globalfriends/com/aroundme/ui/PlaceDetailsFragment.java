package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.GooglePlaceDetailsJson;
import com.globalfriends.com.aroundme.data.places.Places;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;
import com.globalfriends.com.aroundme.protocol.places.PlacesWebService;
import com.globalfriends.com.aroundme.utils.Utility;

import testing.MainActivity;

/**
 *
 */
public class PlaceDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PlaceDetailsFragment";
    private ProgressDialog mProgress;
    private OnPlaceDetailsFragmentInteractionListener mListener;
    private ResultResponse mResultListener = new ResultResponse();
    private IPlaceDetails mGooglePlaceDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setMessage(getResources().getString(R.string.please_wait_progress));
        mProgress.isIndeterminate();
        mProgress.show();

        TransactionManager.getInstance().addResultCallback(mResultListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.place_detail_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Places place = bundle.getParcelable("PLACE");
            TransactionManager.getInstance().findPlaceDetails(place.getPlaceId(), null);
            // Update Image
            mMainDisplayImage.setImageUrl(Utility.getPlacePhotoQuery(place.getPhotoReference().getReference(),
                    place.getPhotoReference().getHeight(), place.getPhotoReference().getWidth()),
                    TransactionManager.getInstance().getModuleImageLoader(getResources().getString(R.string.google_places_tag)));
        }
    }


    private TextView mPlaceName;
    private TextView mAddress;
    private TextView mDistance;
    private NetworkImageView mMainDisplayImage;
    private LinearLayout mMapButtonLayout;
    private LinearLayout mCallButtonLayout;
    private LinearLayout mWebsiteButtonLayout;
    private LinearLayout mFavoriteButtonLayout;

    /**
     * Initialize Google and other components specific UI
     *
     * @param view
     */
    private void initView(View view) {
        mMainDisplayImage = (NetworkImageView) view.findViewById(R.id.id_place_image);
        mPlaceName = (TextView) view.findViewById(R.id.id_place_name);
        mMapButtonLayout = (LinearLayout) view.findViewById(R.id.id_maps);
        mMapButtonLayout.setOnClickListener(this);
        mCallButtonLayout = (LinearLayout) view.findViewById(R.id.id_call);
        mCallButtonLayout.setOnClickListener(this);
        mWebsiteButtonLayout = (LinearLayout) view.findViewById(R.id.id_website);
        mWebsiteButtonLayout.setOnClickListener(this);
        mFavoriteButtonLayout = (LinearLayout) view.findViewById(R.id.id_favorite);
        mFavoriteButtonLayout.setOnClickListener(this);
        mAddress = (TextView) view.findViewById(R.id.id_address_text);
        mDistance = (TextView) view.findViewById(R.id.id_distance_text);
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
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mPlaceName.getText());
    }

    @Override
    public void onDetach() {
        TransactionManager.getInstance().removeResultCallback(mResultListener);
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_maps:
                // Launch Maps Activity
                break;
            case R.id.id_call:
                getActivity().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                        mGooglePlaceDetails.getPhoneNumber())));
                break;
            case R.id.id_website:
                // Launch MAP activity
                Intent intent = new Intent(getActivity(), AppBrowser.class);
                intent.putExtra("URL", mGooglePlaceDetails.getWebUrl());
                getActivity().startActivity(intent);
                break;
            case R.id.id_favorite:
                Snackbar.make(v, "Added to Favorite", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateUi() {
        mPlaceName.setText(mGooglePlaceDetails.getPlaceName());
        getActivity().setTitle(mGooglePlaceDetails.getPlaceName());

        mAddress.setText(mGooglePlaceDetails.getAddress());
        mDistance.setText(Utility.distanceFromLatitudeLongitude(Double.valueOf(PreferenceManager.getLatitude()),
                Double.valueOf(PreferenceManager.getLongitude()),
                mGooglePlaceDetails.getLatitude(),
                mGooglePlaceDetails.getLongitude(),
                PreferenceManager.getDistanceFormat()));
    }

    /**
     * Updated supported components such as Yelp, Four Square etc..
     */
    private void updateSupportedComponenet() {

    }

    public interface OnPlaceDetailsFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    /**
     * Response handler for getting detailed informaiton about place
     */
    class ResultResponse extends TransactionManager.Result {
        @Override
        public void onGetPlaceDetails(IPlaceDetails response, String placeTag) {
            Logger.i(TAG, "Response received");
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }

            if (getResources().getString(R.string.google_places_tag).equalsIgnoreCase(placeTag)) {
                // This will make sure that it fetches responses from Yelp and other modules
                mGooglePlaceDetails = response;
                updateUi();
                TransactionManager.getInstance().onGetPlaceDetails(null, response.getPhoneNumber());
            }
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            super.onError(errorMsg, tag);
        }
    }
}
