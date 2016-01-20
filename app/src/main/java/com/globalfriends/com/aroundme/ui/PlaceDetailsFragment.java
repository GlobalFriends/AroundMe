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
import android.support.design.widget.FloatingActionButton;
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
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.provider.AroundMeContractProvider;
import com.globalfriends.com.aroundme.ui.review.ReviewList;
import com.globalfriends.com.aroundme.utils.Utility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;


/**
 * Shows place details view with all data from supported modules such as four square, yelp, google etc.
 */
public class PlaceDetailsFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "PlaceDetailsFragment";
    private ProgressDialog mProgress;
    private OnPlaceDetailsFragmentInteractionListener mListener;
    private ResultResponse mResultListener = new ResultResponse();
    private IPlaceDetails mGooglePlaceDetails;
    private ImageLoader mGoogleImageLoader;
    private String mPlaceId;
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
    private FloatingActionButton mFab;
    private HashSet<String> mDisplayedModuleTags = new HashSet<String>();

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
            mPlaceId = bundle.getString("PLACE_ID");
        }

        if (savedInstanceState != null) {
            mPlaceId = savedInstanceState.getString("PLACE_ID");
        }

        // Fail Safe
        if (TextUtils.isEmpty(mPlaceId)) {
            Log.e(TAG, "For some reason place details are still null. Go back to List Screen");
            Snackbar.make(view, R.string.no_more_results, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Update Image
        mDisplayedModuleTags.clear();
        TransactionManager.getInstance().findGooglePlaceDetails(mPlaceId, null);
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
            if (mGooglePlaceDetails.isOpenNow()) {
                openNow.setText(R.string.open);
                openNow.setTextColor(ColorStateList.valueOf(Color.DKGRAY));
            } else {
                openNow.setText(R.string.closed);
                openNow.setTextColor(ColorStateList.valueOf(Color.RED));
            }
        }

        // Update rating
        if (!TextUtils.isEmpty(mGooglePlaceDetails.getRating())) {
            ratingStars.setRating(Float.valueOf(mGooglePlaceDetails.getRating()));
            ratingText.setText(mGooglePlaceDetails.getRating());
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
        outState.putString("PLACE_ID", mPlaceId);
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
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

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
        bundle.putDouble("LATITUDE", mGooglePlaceDetails.getLatitude());
        bundle.putDouble("LONGITUDE", mGooglePlaceDetails.getLongitude());
        bundle.putString("NAME", mGooglePlaceDetails.getPlaceName());
        bundle.putInt("MAP_TYPE", GoogleMap.MAP_TYPE_NORMAL);
        Fragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.mapView, mapsFragment).commitAllowingStateLoss();
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
                bundle.putDouble("LATITUDE", mGooglePlaceDetails.getLatitude());
                bundle.putDouble("LONGITUDE", mGooglePlaceDetails.getLongitude());
                bundle.putString("NAME", mGooglePlaceDetails.getPlaceName());
                bundle.putInt("MAP_TYPE", GoogleMap.MAP_TYPE_SATELLITE);
                mListener.onMapsViewClicked(bundle);
                break;
            case R.id.id_favorite: {
                double rating = 0;
                if (mGooglePlaceDetails.getRating() != null) {
                    rating = Double.parseDouble(mGooglePlaceDetails.getRating());
                }

                //Should this be moved to Async task ? on activity exit ?
                if (!AroundMeContractProvider.Places.exist(getActivity(), mPlaceId)) {
                    AroundMeContractProvider.Places fav =
                            new AroundMeContractProvider.Places(mGooglePlaceDetails.isOpenNow(), rating,
                                    mGooglePlaceDetails.getLatitude(), mGooglePlaceDetails.getLongitude(), mPlaceId,
                                    mGooglePlaceDetails.getInternationalPhoneNumber(), "",
                                    mGooglePlaceDetails.getAddress(), mGooglePlaceDetails.getPlaceName());
                    fav.save(getContext());
                    updateFavoriteIcon(true);
                } else {
                    AroundMeContractProvider.Places places = new AroundMeContractProvider.Places();
                    places.delete(getContext(), AroundMeContractProvider.PlacesColumns.PLACES_ID + "=?",
                            new String[]{mPlaceId});
                    updateFavoriteIcon(false);
                }
            }
            break;
            case R.id.fab: {
                {
                    double rating = 0;
                    String photoReference = "";
                    if (mGooglePlaceDetails.getRating() != null) {
                        rating = Double.parseDouble(mGooglePlaceDetails.getRating());
                    }

                    if (!AroundMeContractProvider.RecentPlaces.exist(getActivity(), mPlaceId)) {
                        AroundMeContractProvider.RecentPlaces recentPlaces =
                                new AroundMeContractProvider.RecentPlaces(mGooglePlaceDetails.isOpenNow(), rating,
                                        mGooglePlaceDetails.getLatitude(), mGooglePlaceDetails.getLongitude(), mPlaceId,
                                        mGooglePlaceDetails.getInternationalPhoneNumber(), photoReference,
                                        mGooglePlaceDetails.getAddress(), mGooglePlaceDetails.getPlaceName());
                        recentPlaces.save(getContext());
                    }

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + mGooglePlaceDetails.getAddress()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getActivity().startActivity(intent);
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
        if (TextUtils.isEmpty(mGooglePlaceDetails.getInternationalPhoneNumber())) {
            callLayout.setVisibility(View.GONE);
        } else {
            callLayout.setVisibility(View.VISIBLE);
            TextView call = (TextView) getActivity().findViewById(R.id.id_call);
            call.setText(mGooglePlaceDetails.getInternationalPhoneNumber());
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
        final List<String> timeMap = mGooglePlaceDetails.getWeeklyTimings();
        final LinearLayoutCompat weekly_timings = (LinearLayoutCompat) mTimingLayout.
                findViewById(R.id.id_week_timing);
        if (timeMap == null || timeMap.size() == 0) {
            mTimingLayout.setVisibility(View.GONE);
            return;
        }

        mTimingLayout.setVisibility(View.VISIBLE);
        TextView todayTime = (TextView) mTimingLayout.findViewById(R.id.id_today_hours);
        todayTime.append(Utility.getTodayTiming(timeMap));
        final TextView more = (TextView) mTimingLayout.findViewById(R.id.id_hours_more);
        more.setPaintFlags(more.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getResources().getString(R.string.more).
                        equalsIgnoreCase(more.getText().toString())) {
                    if (timeMap == null || timeMap.size() == 0) {
                        return;
                    }

                    more.setText(getActivity().getResources().getString(R.string.closed));
                    weekly_timings.setVisibility(View.VISIBLE);

                    ((TextView) mTimingLayout.findViewById(R.id.id_monday_hours)).setText(timeMap.get(0));
                    ((TextView) mTimingLayout.findViewById(R.id.id_tuesday_hours)).setText(timeMap.get(1));
                    ((TextView) mTimingLayout.findViewById(R.id.id_wednesday_hours)).setText(timeMap.get(2));
                    ((TextView) mTimingLayout.findViewById(R.id.id_thursday_hours)).setText(timeMap.get(3));
                    ((TextView) mTimingLayout.findViewById(R.id.id_friday_hours)).setText(timeMap.get(4));
                    ((TextView) mTimingLayout.findViewById(R.id.id_saturday_hours)).setText(timeMap.get(5));
                    ((TextView) mTimingLayout.findViewById(R.id.id_sunday_hours)).setText(timeMap.get(6));

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
        updateFavoriteIcon(AroundMeContractProvider.Places.exist(getActivity(), mPlaceId));
    }

    /**
     * Update favorite status of place
     *
     * @param isFavorite
     */
    private void updateFavoriteIcon(final boolean isFavorite) {
        //Uncomment after getting proper image
        ImageView img = (ImageView) mFavoriteButtonLayout.findViewById(R.id.id_favorite_image);
        img.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.unfavorite);
    }

    /**
     * Updated supported components such as Yelp, Four Square etc..
     */
    private void updateSupportedComponent() {

    }

    /**
     * Add's dynmic layout for provided module
     *
     * @param placeDetails
     * @param moduleName
     */
    private void addReviewsFromModules(final IPlaceDetails placeDetails, final String moduleName) {
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
        TextView name = (AppCompatTextView) moduleLayout.findViewById(R.id.module_id);
        ImageView moduleLogo = (ImageView) moduleLayout.findViewById(R.id.module_logo);

        if (TransactionManager.getInstance().getModuleCompleteLogo(moduleName) == 0) {
            moduleLogo.setVisibility(View.GONE);
            moduleImage.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            moduleImage.setImageResource(TransactionManager.getInstance().getModuleIcon(moduleName));
            name.setText(moduleName);
        } else {
            moduleLogo.setVisibility(View.VISIBLE);
            moduleImage.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            moduleLogo.setImageResource(TransactionManager.getInstance().getModuleCompleteLogo(moduleName));
        }


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
        AppCompatTextView fourSquareRating = (AppCompatTextView) moduleLayout.findViewById(R.id.four_square_rating_bar);
        if (placeDetails.getRatingUrl() != null) {
            ratingBar.setVisibility(View.GONE);
            fourSquareRating.setVisibility(View.GONE);
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
                fourSquareRating.setVisibility(View.GONE);
                ratingBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(placeDetails.getRating())) {
                    ratingBar.setRating(Float.valueOf(mGooglePlaceDetails.getRating()));
                }
            } else if (moduleName.equalsIgnoreCase(getString(R.string.four_square_tag))) {
                ratingBar.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(placeDetails.getRating())) {
                    fourSquareRating.setVisibility(View.VISIBLE);
                    try {
                        fourSquareRating.setBackgroundColor(Color.parseColor(placeDetails.getPlaceRatingColorCode()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fourSquareRating.setText("" + placeDetails.getRating() + "/10");
                }
            } else {
                fourSquareRating.setVisibility(View.GONE);
                if (TextUtils.isEmpty(placeDetails.getRating())) {
                    ratingBar.setVisibility(View.GONE);
                } else {
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(Float.valueOf(placeDetails.getRating()));
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

        // Module Launch
        LinearLayoutCompat moduleLaunch = (LinearLayoutCompat) moduleLayout.findViewById(R.id.launch_module);
        if (TextUtils.isEmpty(placeDetails.getWebUrl()) || getString(R.string.google_places_tag).equalsIgnoreCase(moduleName)) {
            moduleLaunch.setVisibility(View.GONE);
        } else {
            moduleLaunch.setVisibility(View.VISIBLE);
            AppCompatTextView moduleLaunchText = (AppCompatTextView) moduleLaunch.findViewById(R.id.goto_module);
            moduleLaunchText.setText(String.format(getString(R.string.module_launch), moduleName));
            moduleLaunch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeDetails.getWebUrl()));
                    getActivity().startActivity(browserIntent);
                }
            });
        }

        LinearLayoutCompat container = (LinearLayoutCompat) mParenLayout.findViewById(R.id.
                dynamic_layout_container);
        if (getResources().getString(R.string.yelp_tag).equalsIgnoreCase(moduleName)) {
            container.addView(moduleLayout, 0);
        } else {
            container.addView(moduleLayout);
        }
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
                // If image is not present then show maps view here
                if (response.getPhotos() == null || response.getPhotos().size() == 0) {
                    mMapView.setVisibility(View.VISIBLE);
                    mMainDisplayImage.setVisibility(View.GONE);
                    updateMapView();
                } else {
                    mMapView.setVisibility(View.GONE);
                    mMainDisplayImage.setVisibility(View.VISIBLE);
                    PlacePhotoMetadata metaData = response.getPhotos().get(0);
                    mMainDisplayImage.setImageUrl(
                            Utility.getPlacePhotoQuery(metaData.getReference(),
                                    metaData.getHeight(),
                                    metaData.getWidth()),
                            mGoogleImageLoader);
                }

                updateUi();
                addReviewsFromModules(response, placeTag);
                TransactionManager.getInstance().findPlaceDetails(response.getInternationalPhoneNumber(),
                        response.getPhoneNumber(),
                        response.getLatitude(), response.getLongitude());
                return;
            }

            if (!mDisplayedModuleTags.contains(placeTag)) {
                mDisplayedModuleTags.add(placeTag);
                addReviewsFromModules(response, placeTag);
            }
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            if (TextUtils.isEmpty(tag) ||
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
                    .setCancelable(false)
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
