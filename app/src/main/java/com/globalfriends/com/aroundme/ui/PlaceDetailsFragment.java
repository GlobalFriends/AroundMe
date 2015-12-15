package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.provider.AroundMeContractProvider;
import com.globalfriends.com.aroundme.ui.review.ReviewList;
import com.globalfriends.com.aroundme.utils.Utility;

import java.io.Serializable;
import java.util.List;

;

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
    private TextView mCall;
    private TextView mWebsite;

    // UI Elements
    private TextView mPlaceName;
    private TextView mAddress;
    private TextView mDistance;
    private NetworkImageView mMainDisplayImage;
    private LinearLayoutCompat mMapButtonLayout;
    private LinearLayoutCompat mFavoriteButtonLayout;
    private CardView mGooglePhotosLayout;
    private LinearLayoutCompat mRatingBarLayout;
    private CardView mReviewLayout;
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
        TransactionManager.getInstance().findPlaceDetails(mPlace.getPlaceId(), null);
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
        // Basic and Simple layouts
        mMainDisplayImage = (NetworkImageView) view.findViewById(R.id.id_place_image);
        mPlaceName = (TextView) view.findViewById(R.id.id_place_name);
        mAddress = (TextView) view.findViewById(R.id.id_address);
        mDistance = (TextView) view.findViewById(R.id.distance_id);
        mRatingBarLayout = (LinearLayoutCompat) view.findViewById(R.id.rating_bar_layout);


        // Clickable Layouts
        mMapButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_maps);
        mMapButtonLayout.setOnClickListener(this);
        mCall = (TextView) view.findViewById(R.id.id_call);
        mWebsite = (TextView) view.findViewById(R.id.id_website);
        mFavoriteButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_favorite);
        mFavoriteButtonLayout.setOnClickListener(this);
        mReviewLayout = (CardView) view.findViewById(R.id.review_layout);
        mTimingLayout = (LinearLayoutCompat) view.findViewById(R.id.id_timings);

        mGooglePhotosLayout = (CardView) view.findViewById(R.id.google_photo);
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
            case R.id.id_favorite:
                if (mPlace != null) {
                    AroundMeContractProvider.Places fav =
                            new AroundMeContractProvider.Places(mPlace.isOpenNow(), Double.parseDouble(mPlace.getRating()),
                                    mPlace.getLatitude(), mPlace.getLongitude(), mPlace.getPlaceId(),
                                    mGooglePlaceDetails.getPhoneNumber(), mPlace.getPhotoReference().toString(),
                                    mGooglePlaceDetails.getAddress());
                }
                break;
            case R.id.review_linear_layout:
                Intent reviewIntent = new Intent(getActivity(), ReviewList.class);
                reviewIntent.putExtra("TAG_NAME", getActivity().getResources().getString(R.string.google_places_tag));
                reviewIntent.putExtra("REVIEW_LIST", (Serializable) mGooglePlaceDetails.getReviewList());
                getActivity().startActivity(reviewIntent);
                break;
        }
    }

    /**
     *
     */
    private void updatePlaceDetails() {
        mAddress.setText(mGooglePlaceDetails.getAddress());
        mCall.setText(mGooglePlaceDetails.getPhoneNumber());
        mWebsite.setText(mGooglePlaceDetails.getWebUrl());
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

    /**
     * Update UI elements based on Google response. Update should be planned in such a way that network
     * requested are queued early for quick fetch
     */
    private void updateUi() {
        // Photo loading can take time..So lets first start loading it
        Utility.updateModulePhotoView(getActivity(), mGooglePlaceDetails,
                mGooglePhotosLayout, mGoogleImageLoader);

        // Update details such as Address, Name, rating and distance
        mPlaceName.setText(mGooglePlaceDetails.getPlaceName());
        getActivity().setTitle(mGooglePlaceDetails.getPlaceName());

        updateRatingBar();
        updatePlaceDetails();
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
    private void updateReviewBar(final IPlaceDetails placeDetails, final CardView layout,
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

        LinearLayoutCompat content = (LinearLayoutCompat) layout.findViewById(R.id.review_linear_layout);
        content.setOnClickListener(this);

        AppCompatRatingBar ratingBar = (AppCompatRatingBar) layout.findViewById(R.id.review_rating_bar);
        AppCompatTextView ratingText = (AppCompatTextView) layout.findViewById(R.id.review_rating_text);
        AppCompatTextView ratingTime = (AppCompatTextView) layout.findViewById(R.id.review_rating_time);
        AppCompatTextView authorName = (AppCompatTextView) layout.findViewById(R.id.review_author_name);
        AppCompatTextView reviewComment = (AppCompatTextView) layout.findViewById(R.id.review_comment);

        AppCompatTextView aspectType = (AppCompatTextView) layout.findViewById(R.id.aspect_type_id);
        AppCompatTextView aspectRating = (AppCompatTextView) layout.findViewById(R.id.aspect_rating_id);

        CircularNetworkImageView avatar = (CircularNetworkImageView) layout.findViewById(R.id.review_avatar);

        //Update GUI content with 1st element of List
        final PlaceReviewMetadata data = reviewList.get(0);
        if (data.getProfilePhotoUrl() != null) {
            avatar.setImageUrl(data.getProfilePhotoUrl(), imageLoader);
        } else {
            avatar.setImageBitmap(Utility.getCircularBitmap(BitmapFactory.decodeResource(
                    AroundMeApplication.getContext().getResources(), R.drawable.profile)));
        }

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getAuthorUrl() == null) {
                    return;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getAuthorUrl()));
                getActivity().startActivity(browserIntent);
            }
        });

        ratingBar.setRating(Float.valueOf(data.getRating()));
        ratingText.setText(data.getRating());
        ratingTime.setText(Utility.Epoch2DateString(data.getReviewTime()));
        authorName.setText(data.getAuthorName());
        reviewComment.setText(data.getReviewText());
        aspectType.setText(data.getAspect());
        aspectRating.setText(data.getAspectDescription().getReviewString());
    }

    /**
     * Updated supported components such as Yelp, Four Square etc..
     */
    private void updateSupportedComponent() {

    }

    /**
     * Interface for fragment and activity communication
     */
    public interface OnPlaceDetailsFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        /**
         * @param tag
         */
        void handleFragmentSuicidal(final String tag);
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
//                TransactionManager.getInstance().findPlaceDetails(null, response.getPhoneNumber());
            }
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            if (!TextUtils.isEmpty(tag)) {
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
