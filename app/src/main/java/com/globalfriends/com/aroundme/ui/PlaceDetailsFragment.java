package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.io.Serializable;
import java.util.List;

;

/**
 *
 */
public class PlaceDetailsFragment extends BaseFragment implements View.OnClickListener {
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
    private LinearLayoutCompat mFavoriteButtonLayout;
    private MapView mMapView;
    private CardView mGooglePhotosLayout;
    private LinearLayoutCompat mRatingBarLayout;
    private LinearLayoutCompat mTimingLayout;
    private LinearLayoutCompat mParenLayout;


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
            Snackbar.make(view, R.string.no_more_results, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Update Image
        if (mPlace.getPhotoReference() != null) {
            mMapView.setVisibility(View.GONE);
            mMainDisplayImage.setVisibility(View.VISIBLE);
            mMainDisplayImage.setImageUrl(
                    Utility.getPlacePhotoQuery(mPlace.getPhotoReference().getReference(),
                            mPlace.getPhotoReference().getHeight(),
                            mPlace.getPhotoReference().getWidth()),
                    mGoogleImageLoader);
        } else {
            mMapView.setVisibility(View.VISIBLE);
            mMainDisplayImage.setVisibility(View.GONE);
            updateMapView();
        }
        TransactionManager.getInstance().findGooglePlaceDetails(mPlace.getPlaceId(), null);
    }

    /**
     * Handle Place Rating symbol, and closure status
     */
    private void updateRatingBar() {
        AppCompatRatingBar ratingStars = (AppCompatRatingBar) mRatingBarLayout.findViewById(R.id.id_rating_star);
        TextView ratingText = (TextView) mRatingBarLayout.findViewById(R.id.id_rating_text);
        TextView openNow = (TextView) mRatingBarLayout.findViewById(R.id.id_open_now_text);

        // Update open or close status
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

        // Update rating
        if (!TextUtils.isEmpty(mPlace.getRating())) {
            ratingStars.setRating(Float.valueOf(mPlace.getRating()));
            ratingText.setText(mPlace.getRating());
        } else {
            ratingText.setText(getResources().getString(R.string.not_rated));
        }

        // Update distance
        mDistance.setText(Utility.distanceFromLatitudeLongitude(Double.valueOf(PreferenceManager.getLatitude()),
                Double.valueOf(PreferenceManager.getLongitude()),
                mGooglePlaceDetails.getLatitude(),
                mGooglePlaceDetails.getLongitude(),
                PreferenceManager.getDistanceFormat()));
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
        mParenLayout = (LinearLayoutCompat) view.findViewById(R.id.parent_layout);
        // Basic and Simple layouts
        mMainDisplayImage = (NetworkImageView) view.findViewById(R.id.id_place_image);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mPlaceName = (TextView) view.findViewById(R.id.id_place_name);
        mAddress = (TextView) view.findViewById(R.id.id_address);
        mDistance = (TextView) view.findViewById(R.id.distance_id);
        mRatingBarLayout = (LinearLayoutCompat) view.findViewById(R.id.rating_bar_layout);


        // Clickable Layouts
        mMapButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_maps);
        mMapButtonLayout.setOnClickListener(this);
        mFavoriteButtonLayout = (LinearLayoutCompat) view.findViewById(R.id.id_favorite);
        mFavoriteButtonLayout.setOnClickListener(this);
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

    /**
     * Update Map view for specified lattitude and longitude
     */
    private void updateMapView() {
        Bundle bundle = new Bundle();
        bundle.putDouble("LATITUDE", mPlace.getLatitude());
        bundle.putDouble("LONGITUDE", mPlace.getLongitude());
        bundle.putString("NAME", mPlace.getName());
        bundle.putInt("MAP_TYPE", GoogleMap.MAP_TYPE_NORMAL);
        Fragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.mapView, mapsFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbarUpdater.onNavigationEnabled(false);
        mToolbarUpdater.onSearchBarEnabled(false);
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
                Bundle bundle = new Bundle();
                bundle.putDouble("LATITUDE", mPlace.getLatitude());
                bundle.putDouble("LONGITUDE", mPlace.getLongitude());
                bundle.putString("NAME", mPlace.getName());
                bundle.putInt("MAP_TYPE", GoogleMap.MAP_TYPE_SATELLITE);
                mListener.onMapsViewClicked(bundle);
                break;
            case R.id.id_favorite:
                if (mPlace != null) {
                    double rating = 0;
                    String photoReference = "";
                    if (mPlace.getRating() != null) {
                        rating = Double.parseDouble(mPlace.getRating());
                    }
                    if (mPlace.getPhotoReference() != null) {
                        photoReference = mPlace.getPhotoReference().toString();
                    }
                    //Should this be moved to Async task ? on activity exit ?
                    if (!AroundMeContractProvider.Places.exist(getActivity(), mPlace.getPlaceId())) {
                        AroundMeContractProvider.Places fav =
                                new AroundMeContractProvider.Places(mPlace.isOpenNow(), rating,
                                        mPlace.getLatitude(), mPlace.getLongitude(), mPlace.getPlaceId(),
                                        mGooglePlaceDetails.getPhoneNumber(), photoReference,
                                        mGooglePlaceDetails.getAddress(), mPlace.getName());
                        fav.save(getContext());
                        LinearLayoutCompat ll = (LinearLayoutCompat) v;
                        //Uncomment after getting proper image
//                        ImageView img = (ImageView)ll.findViewById(R.id.id_favorite_image);
//                        img.setBackgroundResource(R.drawable.fav);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Update place detail such as website, phone, distance ets.
     */
    private void updatePlaceDetails() {
        mAddress.setText(mGooglePlaceDetails.getAddress());
        LinearLayoutCompat callLayout = (LinearLayoutCompat) getActivity().findViewById(R.id.call_layout);
        if (TextUtils.isEmpty(mGooglePlaceDetails.getPhoneNumber())) {
            callLayout.setVisibility(View.GONE);
        } else {
            callLayout.setVisibility(View.VISIBLE);
            TextView call = (TextView) getActivity().findViewById(R.id.id_call);
            call.setText(mGooglePlaceDetails.getPhoneNumber());
        }

        LinearLayoutCompat websiteLayout = (LinearLayoutCompat) getActivity().findViewById(R.id.website_layout);
        if (TextUtils.isEmpty(mGooglePlaceDetails.getWebUrl())) {
            websiteLayout.setVisibility(View.GONE);
        } else {
            websiteLayout.setVisibility(View.VISIBLE);
            TextView mWebsite = (TextView) getActivity().findViewById(R.id.id_website);
            mWebsite.setText(mGooglePlaceDetails.getWebUrl());
        }
    }

    /**
     * Update place timing details
     */
    private void updatePlaceTiming() {
        final List<String> timMap = mGooglePlaceDetails.getWeeklyTimings();
        final LinearLayoutCompat weekly_timings = (LinearLayoutCompat) mTimingLayout.
                findViewById(R.id.id_week_timing);
        if (timMap == null || timMap.size() == 0) {
            mTimingLayout.setVisibility(View.GONE);
            return;
        }

        mTimingLayout.setVisibility(View.VISIBLE);
        final TextView more = (TextView) mTimingLayout.findViewById(R.id.id_hours_more);
        more.setPaintFlags(more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getResources().getString(R.string.more).
                        equalsIgnoreCase(more.getText().toString())) {
                    more.setText(getActivity().getResources().getString(R.string.closed));
                    weekly_timings.setVisibility(View.VISIBLE);
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
     * Update UI elements based on Google response. Upadate should be planned in such a way that network
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
         * Move to maps view
         *
         * @param bundle
         */
        void onMapsViewClicked(Bundle bundle);

        /**
         * @param tag
         */
        void handleFragmentSuicidal(final String tag);
    }

    /**
     * Add's dynmic layout for provided module
     *
     * @param placeDetails
     * @param moduleName
     */
    private void addDynamicView(final IPlaceDetails placeDetails, final String moduleName) {
        if (TextUtils.isEmpty(moduleName)) {
            Log.e(TAG, "module name is empty");
            return;
        }

        if (placeDetails.getReviewCount() <= 0) {
            Log.e(TAG, "There are not reviews to display");
            return;
        }

        // Keep review list ready. If will be used for multiple references
        final List<PlaceReviewMetadata> reviewList = placeDetails.getReviewList();

        ImageLoader imageLoader = TransactionManager.getInstance().getModuleImageLoader(moduleName);
        LayoutInflater layoutInflater =
                (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout moduleLayout = (LinearLayout) layoutInflater.inflate(R.layout.module_layout, null);
        if (moduleLayout == null) {
            return;
        }

        // First Row
        ImageView moduleImage = (ImageView) moduleLayout.findViewById(R.id.module_image);
        moduleImage.setImageResource(TransactionManager.getInstance().getModuleIcon(moduleName));
        TextView name = (AppCompatTextView) moduleLayout.findViewById(R.id.module_id);
        name.setText(moduleName);
        TextView more = (AppCompatTextView) moduleLayout.findViewById(R.id.module_more);

        more.setPaintFlags(more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reviewList.size() > 1) {
                    Intent reviewIntent = new Intent(getActivity(), ReviewList.class);
                    reviewIntent.putExtra("TAG_NAME", moduleName);
                    reviewIntent.putExtra("REVIEW_LIST", (Serializable) reviewList);
                    getActivity().startActivity(reviewIntent);
                } else {
                    if (TextUtils.isEmpty(placeDetails.getWebUrl())) {
                        return;
                    }
                    // Otherwise launch provided URL.
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeDetails.getWebUrl()));
                    getActivity().startActivity(browserIntent);
                }
            }
        });


        // 2nd Row with Review Icons and review counts
        NetworkImageView ratingImageView = (NetworkImageView) moduleLayout.findViewById(R.id.review_rating_id);
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) moduleLayout.findViewById(R.id.review_rating_bar);
        AppCompatTextView ratingCount = (AppCompatTextView) moduleLayout.findViewById(R.id.rating_count_text);
        if (placeDetails.getRatingUrl() != null) {
            ratingBar.setVisibility(View.GONE);
            if (TextUtils.isEmpty(placeDetails.getRatingUrl())) {
                ratingImageView.setVisibility(View.GONE);
            } else {
                ratingImageView.setVisibility(View.VISIBLE);
                ratingImageView.setImageUrl(placeDetails.getRatingUrl(), imageLoader);
            }
        } else {
            ratingImageView.setVisibility(View.GONE);
            /**
             * Google rating is returned in place list, but details are empty.
             * So, we have to handle below exception
             */
            if (moduleName.equalsIgnoreCase(getString(R.string.google_places_tag))) {
                ratingBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mPlace.getRating())) {
                    ratingBar.setRating(Float.valueOf(mPlace.getRating()));
                }
            } else {
                if (TextUtils.isEmpty(placeDetails.getPlaceRating())) {
                    ratingBar.setVisibility(View.GONE);
                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(Float.valueOf(placeDetails.getPlaceRating()));
                    if (placeDetails.getRatingColor() != 0) {
                        ratingBar.getProgressDrawable().setTint(placeDetails.getRatingColor());
                    }
                }
            }
        }
        ratingCount.setText(String.format(getString(R.string.review_count), placeDetails.getReviewCount()));

        //3rd Row with Review Profile and Review content
        CircularNetworkImageView reviewerImage = (CircularNetworkImageView) moduleLayout.findViewById(R.id.reviewer_image);
        TextView authorName = (AppCompatTextView) moduleLayout.findViewById(R.id.review_author_name);
        TextView reviewContent = (AppCompatTextView) moduleLayout.findViewById(R.id.review_content);

        if (reviewList.size() > 0) {
            // Set Review icon
            final PlaceReviewMetadata metaData = reviewList.get(0);
            if (metaData.getProfilePhotoUrl() != null) {
                reviewerImage.setImageUrl(metaData.getProfilePhotoUrl(), imageLoader);
            } else {
                reviewerImage.setImageBitmap(Utility.getCircularBitmap(BitmapFactory.decodeResource(
                        AroundMeApplication.getContext().getResources(), R.drawable.profile)));
            }


            // Set Reviewer name
            if (TextUtils.isEmpty(metaData.getAuthorName())) {
                authorName.setVisibility(View.GONE);
            } else {
                authorName.setVisibility(View.VISIBLE);
                authorName.setText(metaData.getAuthorName());
            }

            // Set Review content
            reviewerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (metaData.getAuthorUrl() == null) {
                        return;
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(metaData.getAuthorUrl()));
                    getActivity().startActivity(browserIntent);
                }
            });

            reviewContent.setText(metaData.getReviewText());

            // Over all row on click handle
            reviewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reviewList.size() > 1) {
                        Intent reviewIntent = new Intent(getActivity(), ReviewList.class);
                        reviewIntent.putExtra("TAG_NAME", moduleName);
                        reviewIntent.putExtra("REVIEW_LIST", (Serializable) reviewList);
                        getActivity().startActivity(reviewIntent);
                    } else {
                        if (TextUtils.isEmpty(placeDetails.getWebUrl())) {
                            return;
                        }
                        // Otherwise launch provided URL.
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeDetails.getWebUrl()));
                        getActivity().startActivity(browserIntent);
                    }
                }
            });
        }

        LinearLayoutCompat container = (LinearLayoutCompat) mParenLayout.findViewById(R.id.
                dynamic_layout_container);
        container.addView(moduleLayout);
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
                addDynamicView(response, placeTag);
                TransactionManager.getInstance().findPlaceDetails(response.getPhoneNumber(),
                        response.getLatitude(), response.getLongitude());
                return;
            }

            addDynamicView(response, placeTag);
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            if (!TextUtils.isEmpty(tag) ||
                    !getActivity().getString(R.string.google_places_tag).equalsIgnoreCase(tag)) {
                Log.e(TAG, "This is not a google response. Just ignore it");
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
