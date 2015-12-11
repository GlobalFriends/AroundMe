package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.provider.AroundMeContractProvider;
import com.globalfriends.com.aroundme.ui.review.ReviewList;
import com.globalfriends.com.aroundme.utils.Utility;

;import java.io.Serializable;
import java.util.List;

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
    private LinearLayoutCompat mReviewLayout;
    private LinearLayoutCompat mTimingLayout;


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
        } else {
            mMainDisplayImage.setImageUrl(mPlace.getIcon(), mGoogleImageLoader);
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
            if (mPlace.isOpenNow()) {
                openNow.setText(R.string.open);
                openNow.setTextColor(ColorStateList.valueOf(Color.DKGRAY));
            } else {
                openNow.setText(R.string.closed);
                openNow.setTextColor(ColorStateList.valueOf(Color.RED));
            }
        }

        if (!TextUtils.isEmpty(mPlace.getRating())) {
            ratingStars.setRating(Float.valueOf(mPlace.getRating()));
            ratingText.setText(mPlace.getRating());
        } else {
            ratingText.setText(getResources().getString(R.string.not_rated));
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
        mReviewLayout = (LinearLayoutCompat) view.findViewById(R.id.review_layout);
        mTimingLayout = (LinearLayoutCompat) view.findViewById(R.id.id_timings);

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
//                Snackbar.make(v, "Added to Favorite", Snackbar.LENGTH_SHORT).show();
                if (mPlace != null) {
                    AroundMeContractProvider.Places fav =
                            new AroundMeContractProvider.Places(mPlace.isOpenNow(), Double.parseDouble(mPlace.getRating()),
                                    mPlace.getLatitude(), mPlace.getLongitude(), mPlace.getPlaceId(),
                                    mGooglePlaceDetails.getPhoneNumber(), mPlace.getPhotoReference().toString(),
                                    mGooglePlaceDetails.getAddress());
                }
                break;
            case R.id.review_content_layout:
                Intent reviewIntent = new Intent(getActivity(), ReviewList.class);
                reviewIntent.putExtra("TAG_NAME", getActivity().getResources().getString(R.string.google_places_tag));
                reviewIntent.putExtra("REVIEW_LIST", (Serializable) mGooglePlaceDetails.getReviewList());
                getActivity().startActivity(reviewIntent);
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

    /**
     * Update place timing details
     */
    private void updatePlaceTiming() {
        final TextView more = (TextView) mTimingLayout.findViewById(R.id.id_hours_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutCompat weekly_timings = (LinearLayoutCompat) mTimingLayout.
                        findViewById(R.id.id_week_timing);
                if (getActivity().getResources().getString(R.string.more).
                        equalsIgnoreCase(more.getText().toString())) {
                    more.setText(getActivity().getResources().getString(R.string.closed));
                    weekly_timings.setVisibility(View.VISIBLE);
                    List<String> timMap = mGooglePlaceDetails.getWeeklyTimings();
                    if (timMap == null || timMap.size() == 0) {
                        return;
                    }

                    ((TextView) mTimingLayout.findViewById(R.id.id_monday_hours)).setText(timMap.get(0));
                    ((TextView) mTimingLayout.findViewById(R.id.id_tuesday_hours)).setText(timMap.get(1));
                    ((TextView) mTimingLayout.findViewById(R.id.id_wednesday_hours)).setText(timMap.get(2));
                    ((TextView) mTimingLayout.findViewById(R.id.id_thursday_hours)).setText(timMap.get(3));
                    ((TextView) mTimingLayout.findViewById(R.id.id_friday_hours)).setText(timMap.get(4));
                    ((TextView) mTimingLayout.findViewById(R.id.id_saturday_hours)).setText(timMap.get(5));
                    ((TextView) mTimingLayout.findViewById(R.id.id_sunday_hours)).setText(timMap.get(6));

                } else {
                    more.setText(getActivity().getResources().getString(R.string.more));
                    weekly_timings.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUi() {
        // Photo loading can take time..So lets first start loading it
        Utility.updateModulePhotoView(getActivity(), mGooglePlaceDetails,
                mGooglePhotosLayout, mGoogleImageLoader);

        // Update details such as Address, Name, rating and distance
        mPlaceName.setText(mGooglePlaceDetails.getPlaceName());
        getActivity().setTitle(mGooglePlaceDetails.getPlaceName());

        updateRatingBar();
        updateAddressAndDistance();
        updatePlaceTiming();
        updateReviewBar(mGooglePlaceDetails, mReviewLayout, mGoogleImageLoader);
    }

    /**
     * General layout, ric place to create and update Review layout
     *
     * @param placeDetails
     * @param layout
     * @param imageLoader
     */
    private void updateReviewBar(final IPlaceDetails placeDetails, final LinearLayoutCompat layout,
                                 final ImageLoader imageLoader) {
        List<PlaceReviewMetadata> reviewList = placeDetails.getReviewList();
        if (reviewList == null || reviewList.size() == 0) {
            layout.setVisibility(View.GONE);
            return;
        }
        layout.setVisibility(View.VISIBLE);
        // Update header
        AppCompatTextView review_cont = (AppCompatTextView) layout.findViewById(R.id.review_count);
        review_cont.append(" (" + reviewList.size() + ")");

        LinearLayoutCompat content = (LinearLayoutCompat) layout.findViewById(R.id.review_content_layout);
        content.setOnClickListener(this);

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) layout.findViewById(R.id.review_rating_bar);
        AppCompatTextView ratingText = (AppCompatTextView) layout.findViewById(R.id.review_rating_text);
        AppCompatTextView ratingTime = (AppCompatTextView) layout.findViewById(R.id.review_rating_time);
        AppCompatTextView authorName = (AppCompatTextView) layout.findViewById(R.id.review_author_name);
        AppCompatTextView reviewComment = (AppCompatTextView) layout.findViewById(R.id.review_comment);
        NetworkImageView avatar = (NetworkImageView) layout.findViewById(R.id.review_avatar);

        //Update GUI content with 1st element of List
        PlaceReviewMetadata data = reviewList.get(0);
        avatar.setImageUrl(data.getmAuthorUrl(), imageLoader);
        ratingBar.setRating(Float.valueOf(data.getRating()));
        ratingText.setText(data.getRating());
        ratingTime.setText(Utility.getDate(data.getReviewTime()));
        authorName.setText(data.getAuthorName());
        reviewComment.setText(data.getReviewText());
    }

    /**
     * Updated supported components such as Yelp, Four Square etc..
     */
    private void updateSupportedComponent() {

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
            if (getResources().getString(R.string.google_places_tag).equalsIgnoreCase(placeTag)) {
                // Progress bar should be dismissed only for Google Place Manager response
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }

                // This will make sure that it fetches responses from Yelp and other modules
                mGooglePlaceDetails = response;
                mGooglePlaceDetails.toString();
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
