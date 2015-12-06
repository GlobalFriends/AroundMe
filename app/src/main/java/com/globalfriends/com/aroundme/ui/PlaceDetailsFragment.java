package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.utils.Utility;

;import java.util.List;

/**
 *
 */
public class PlaceDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PlaceDetailsFragment";
    private ProgressDialog mProgress;
    private OnPlaceDetailsFragmentInteractionListener mListener;
    private ResultResponse mResultListener = new ResultResponse();
    private IPlaceDetails mGooglePlaceDetails;
    private PlaceInfo mPlace = new PlaceInfo();
    private ImageLoader mGoogleImageLoader;

    // UI Elements
    private TextView mPlaceName;
    private TextView mAddress;
    private TextView mDistance;
    private NetworkImageView mMainDisplayImage;
    private LinearLayoutCompat mMapButtonLayout;
    private LinearLayoutCompat mCallButtonLayout;
    private LinearLayoutCompat mWebsiteButtonLayout;
    private LinearLayoutCompat mFavoriteButtonLayout;
    private LinearLayoutCompat mGooglePhotosLayout;
    private LinearLayoutCompat mRatingBarLayout;


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
    public void onViewCreated(View view, Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGoogleImageLoader = TransactionManager.getInstance().getModuleImageLoader
                (getResources().getString(R.string.google_places_tag));
        initView(view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPlace = bundle.getParcelable("PLACE");
        }

        if (savedInstanceState != null) {
            mPlace = savedInstanceState.getParcelable("PLACE");
        }

        // Fail Safe
        if (mPlace == null) {
            Log.e(TAG, "For some reason place details are still null. Go back to List Screen");
            Snackbar.make(view, "No place Details", Snackbar.LENGTH_SHORT).show();
            return;
        }

        TransactionManager.getInstance().findPlaceDetails(mPlace.getPlaceId(), null);
        // Update Image
        if (mPlace.getPhotoReference() != null) {
            mMainDisplayImage.setImageUrl(
                    Utility.getPlacePhotoQuery(mPlace.getPhotoReference().getReference(),
                            mPlace.getPhotoReference().getHeight(),
                            mPlace.getPhotoReference().getWidth()),
                    mGoogleImageLoader);
        }
    }

    /**
     * Handle Place Rating symbol, and closure status
     */
    private void updateRatingBar() {
        AppCompatRatingBar ratingStars = (AppCompatRatingBar) mRatingBarLayout.findViewById(R.id.id_rating_star);
        TextView ratingText = (TextView) mRatingBarLayout.findViewById(R.id.id_rating_text);
        TextView openNow = (TextView) mRatingBarLayout.findViewById(R.id.id_open_now_text);

        if (mGooglePlaceDetails.isPermanentlyClosed()) {
            openNow.setText(getActivity().getResources().getString(R.string.permanently_closed));
        } else {
            openNow.setText(mPlace.isOpenNow() ? getActivity().getResources().getString(R.string.open) :
                    getActivity().getResources().getString(R.string.closed));

        }

        ratingStars.setVisibility(View.VISIBLE);
        ratingText.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mPlace.getRating())) {
            ratingStars.setRating(Float.valueOf(mPlace.getRating()));
            ratingText.setText(mPlace.getRating());
        } else {
            ratingStars.setVisibility(View.INVISIBLE);
            ratingText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("PLACE", mPlace);
    }

    /**
     * Initialize Google and other components specific UI
     *
     * @param view
     */
    private void initView(View view) {
        mMainDisplayImage = (NetworkImageView) view.findViewById(R.id.id_place_image);
        mPlaceName = (TextView) view.findViewById(R.id.id_place_name);
        mAddress = (TextView) view.findViewById(R.id.id_address_text);
        mDistance = (TextView) view.findViewById(R.id.id_distance_text);

        // Clickable Layouts
        mMapButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_maps);
        mMapButtonLayout.setOnClickListener(this);
        mCallButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_call);
        mCallButtonLayout.setOnClickListener(this);
        mWebsiteButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_website);
        mWebsiteButtonLayout.setOnClickListener(this);
        mFavoriteButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_favorite);
        mFavoriteButtonLayout.setOnClickListener(this);

        // Non Clickable Layouts
        mGooglePhotosLayout = (LinearLayoutCompat) view.findViewById(R.id.google_photo);
        mRatingBarLayout = (LinearLayoutCompat) view.findViewById(R.id.rating_bar_layout);
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

    private void updateAddressAndDistance() {
        mAddress.setText(mGooglePlaceDetails.getAddress());
        mDistance.setText(Utility.distanceFromLatitudeLongitude(Double.valueOf(PreferenceManager.getLatitude()),
                Double.valueOf(PreferenceManager.getLongitude()),
                mGooglePlaceDetails.getLatitude(),
                mGooglePlaceDetails.getLongitude(),
                PreferenceManager.getDistanceFormat()));

    }

    private void updateUi() {
        // Photo loading can take time..SO lets first start loading it
        updateModulePhotoView(mGooglePhotosLayout, mGoogleImageLoader);

        // Update details such as Address, Name, rating and distance
        mPlaceName.setText(mGooglePlaceDetails.getPlaceName());
        getActivity().setTitle(mGooglePlaceDetails.getPlaceName());

        updateRatingBar();
        updateAddressAndDistance();
    }


    /**
     * @param layout
     * @param imageLoader
     */
    private void updateModulePhotoView(final LinearLayoutCompat layout, final ImageLoader imageLoader) {
        List<PlacePhotoMetadata> mList = mGooglePlaceDetails.getPhotos();
        if (mList == null || mList.size() == 0) {
            layout.setVisibility(View.GONE);
            return;
        }

        layout.setVisibility(View.VISIBLE);
        LinearLayoutCompat imageGallery = (LinearLayoutCompat) layout.findViewById(R.id.imageGallery);
        for (PlacePhotoMetadata photo : mList) {
            imageGallery.addView(dynamicGoogleImageView(photo, imageLoader));
        }
    }

    /**
     * Dynamic Image View addintion for requested modules. Passed image module should be proper for requested module
     *
     * @param image
     * @param imageLoader
     * @return
     */
    private NetworkImageView dynamicGoogleImageView(final PlacePhotoMetadata image, final ImageLoader imageLoader) {
        final NetworkImageView imageView = new NetworkImageView(getActivity());
        LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(
                (int) Utility.getDpToPixel(getActivity(), 100),
                (int) Utility.getDpToPixel(getActivity(), 100));
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(imageView, "Handle Image click", Snackbar.LENGTH_SHORT).show();
            }
        });
        imageView.setImageUrl(
                Utility.getPlacePhotoQuery(image.getReference(),
                        (int) Utility.getDpToPixel(getActivity(), 100),
                        (int) Utility.getDpToPixel(getActivity(), 100)),
                imageLoader);
        return imageView;
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
