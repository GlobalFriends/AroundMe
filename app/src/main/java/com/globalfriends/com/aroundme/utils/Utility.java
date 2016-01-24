package com.globalfriends.com.aroundme.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
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
import com.globalfriends.com.aroundme.protocol.places.PlaceRequestTypeEnum;
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

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;

/**
 * Created by vishal on 11/22/2015.
 */
public class Utility {
    public static final boolean DEBUG = false;
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
        if (!DEBUG) {
            return;
        }

        try {
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
                dist = ((dist * 60 * 1.1515) * 1.609344);
                return (String.format("%.2f ", dist) + AroundMeApplication.getContext().
                        getResources().getString(R.string.kilometer_notation));
            default:
        }

        return String.format("%.2f", dist);
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
    public static Double distanceFromLatitudeLongitudeInMeters(double lat1, double lon1, double lat2,
                                                               double lon2, final DistanceFormatEnum type) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        switch (type) {
            case MILES:
                return (dist * 60 * 1.1515);
            case KILOMETER:
                return ((dist * 60 * 1.1515) * 1.609344);
            default:
        }

        return dist;
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
     * Returns today's string from timing map
     *
     * @param timeMap
     * @return
     */
    public static String getTodayTiming(final List<String> timeMap) {
        if (timeMap == null || timeMap.size() == 0) {
            return null;
        }

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String timing;
        switch (day) {
            case 1: //Sunday
                timing = timeMap.get(6);
                return timing.substring(timing.indexOf(":") + 1);
            case 2:
                timing = timeMap.get(0);
                return timing.substring(timing.indexOf(":") + 1);
            case 3:
                timing = timeMap.get(1);
                return timing.substring(timing.indexOf(":") + 1);
            case 4:
                timing = timeMap.get(2);
                return timing.substring(timing.indexOf(":") + 1);
            case 5:
                timing = timeMap.get(3);
                return timing.substring(timing.indexOf(":") + 1);
            case 6:
                timing = timeMap.get(4);
                return timing.substring(timing.indexOf(":") + 1);
            case 7:
                timing = timeMap.get(5);
                return timing.substring(timing.indexOf(":") + 1);
            default:
                return null;
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
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        return format.format(updatedate);
    }

    /**
     * Creates a circular bitmap and uses whichever dimension is smaller to determine the width
     * <br/>Also constrains the circle to the leftmost part of the image
     *
     * @param bitmap
     * @return bitmap
     */
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int width = bitmap.getWidth();
        if (bitmap.getWidth() > bitmap.getHeight())
            width = bitmap.getHeight();
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, width);
        final RectF rectF = new RectF(rect);
        final float roundPx = width / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
