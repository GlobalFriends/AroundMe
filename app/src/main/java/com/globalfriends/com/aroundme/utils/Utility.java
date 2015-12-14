package com.globalfriends.com.aroundme.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ParseException;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.DistanceFormatEnum;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;
import com.globalfriends.com.aroundme.protocol.places.PlaceResponseEnum;
import com.globalfriends.com.aroundme.protocol.places.PlacesWebService;
import com.globalfriends.com.aroundme.ui.PhotoViewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.util.TypedValue.*;

/**
 * Created by vishal on 11/22/2015.
 */
public class Utility {
    private static final String TAG = " Utility";

    /**
     * @param zipCode
     * @return
     */
    public static String getLocationFromZip(final String zipCode) {
        //TODO: We might have to use GeoCoding from PlaceInfo Maps API
        return null;
    }

    public static void generateNoteOnSD(String sFileName, String sBody) {
        try {
            Logger.i(TAG, "Response=" + sBody);
            File root = new File(Environment.getExternalStorageDirectory(), "AroundMe");
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, sFileName + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns your Zip code based on latitude and longitude
     *
     * @return
     */
    public static String getZipCode() {
        try {
            Geocoder gcd = new Geocoder(AroundMeApplication.getContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(Double.valueOf(PreferenceManager.getLatitude()),
                    Double.valueOf(PreferenceManager.getLongitude()), 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getPostalCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns your country code based on latitude and longitude
     *
     * @return
     */
    public static String getCountryCodeFromLocation() {
        try {
            Geocoder gcd = new Geocoder(AroundMeApplication.getContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(Double.valueOf(PreferenceManager.getLatitude()),
                    Double.valueOf(PreferenceManager.getLongitude()), 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CFInds mDistance between 2 places based on latitude and longitude
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static String distanceFromLatitudeLongitude(double lat1, double lon1, double lat2,
                                                       double lon2, final DistanceFormatEnum type) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        switch (type) {
            case MILES:
                dist = (dist * 60 * 1.1515);
                return (String.format("%.2f ", dist) + AroundMeApplication.getContext().
                        getResources().getString(R.string.miles_notation));
            case KILOMETER:
                dist = (dist * 1.609344);
                return (String.format("%.2f ", dist) + AroundMeApplication.getContext().
                        getResources().getString(R.string.kilometer_notation));
            default:
        }

        return String.format("%.2f", dist);
    }

    public static double distanceInMeters(final DistanceFormatEnum type, final int unit) {
        double meters = 0;
        switch (type) {
            case MILES:
                meters = (unit * 1609.34);
                break;
            case KILOMETER:
                meters = (meters * 1000);
                break;
            default:
        }
        return meters;
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * @param photoReference
     * @param maxHeight
     * @param maxWidth
     */
    public static String getPlacePhotoQuery(final String photoReference, final int maxHeight, final int maxWidth) {
        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_TYPE_PHOTO).
                        setPhotoHeight(maxHeight).
                        setPhotoWidth(maxWidth).
                        setPhotoReference(photoReference).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        return builder.build().getUrl();
    }

    /**
     * @param placeId
     * @param contactNumber
     * @return
     */
    public static String findPlaceDetailQuery(String placeId, String contactNumber) {
        if (TextUtils.isEmpty(placeId)) {
            return null;
        }

        PlacesWebService.Builder builder =
                new PlacesWebService.Builder().
                        setSearchType(PlaceRequestTypeEnum.SEARCH_TYPE_DETAILS).
                        setResponseType(PlaceResponseEnum.RESP_JSON).
                        setPlaceId(placeId).
                        setKey(AroundMeApplication.getContext().
                                getResources().getString(R.string.google_maps_key));
        return builder.build().getUrl();
    }

    public static float getDpToPixel(Context context, final int dp) {
        Resources r = context.getResources();
        return applyDimension(COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


    /**
     * @param layout
     * @param imageLoader
     */
    public static void updateModulePhotoView(final Context context, final IPlaceDetails placeDetails,
                                             final CardView layout, final ImageLoader imageLoader) {
        List<PlacePhotoMetadata> mList = placeDetails.getPhotos();
        if (mList == null || mList.size() == 0) {
            layout.setVisibility(View.GONE);
            return;
        }

        layout.setVisibility(View.VISIBLE);
        LinearLayoutCompat imageGallery = (LinearLayoutCompat) layout.findViewById(R.id.imageGallery);
        for (PlacePhotoMetadata photo : mList) {
            imageGallery.addView(addDynamicImageView(context, photo, imageLoader, mList));
        }
    }

    /**
     * Dynamic Image View addition for requested modules. Passed image module should be proper for requested module
     *
     * @param context
     * @param image
     * @param imageLoader
     * @param list        TODO: Should check how to remove this
     * @return
     */
    public static NetworkImageView addDynamicImageView(final Context context, final PlacePhotoMetadata image,
                                                       final ImageLoader imageLoader, final List<PlacePhotoMetadata> list) {
        final NetworkImageView imageView = new NetworkImageView(context);
        LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(
                (int) Utility.getDpToPixel(context, 100),
                (int) Utility.getDpToPixel(context, 100));
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoViewer.class);
                intent.putExtra("KEY", context.getResources().getString(R.string.google_places_tag));
                intent.putExtra("CURRENT_PHOTO", image);
                intent.putExtra("PHOTO_LIST", (Serializable) list);
                context.startActivity(intent);
            }
        });
        imageView.setImageUrl(
                Utility.getPlacePhotoQuery(image.getReference(),
                        (int) Utility.getDpToPixel(context, 100),
                        (int) Utility.getDpToPixel(context, 100)),
                imageLoader);
        return imageView;
    }

    public static String Epoch2DateString(long epochSeconds) {
        Date updatedate = new Date(epochSeconds * 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(updatedate);
    }
}
