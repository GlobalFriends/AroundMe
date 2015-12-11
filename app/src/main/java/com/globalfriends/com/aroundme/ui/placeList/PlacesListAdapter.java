package com.globalfriends.com.aroundme.ui.placeList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.utils.Utility;

import java.util.List;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListAdapter extends ArrayAdapter<PlaceInfo> {
    Context mContext;
    List<PlaceInfo> mPlaces;
    ImageLoader mImageLoader = TransactionManager.getInstance().
            getModuleImageLoader(AroundMeApplication.getContext().getResources().getString(R.string.google_places_tag));

    public PlacesListAdapter(Context context, List<PlaceInfo> objects) {
        super(context, R.layout.layout_places_item, objects);
        mContext = context;
        mPlaces = objects;
    }

    public void swapItem(List<PlaceInfo> object) {
        mPlaces.addAll(object);
        notifyDataSetChanged();
        // Request all images in network..
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_places_item, null);

            viewHolder = new ViewHolder();
            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.mPlacePhoto = (NetworkImageView) convertView.findViewById(R.id.photo);
            viewHolder.mPlaceName = (TextView) convertView.findViewById(R.id.place_name);
            viewHolder.mAddress = (TextView) convertView.findViewById(R.id.vicinity);
            viewHolder.mOpenNow = (TextView) convertView.findViewById(R.id.open_now);
            viewHolder.mDistance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.mRatingText = (TextView) convertView.findViewById(R.id.rating);
            viewHolder.mRatingBar = (AppCompatRatingBar) convertView.findViewById(R.id.rating_star);
            viewHolder.mPriceLevel = (LinearLayout) convertView.findViewById(R.id.price_level);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PlaceInfo place = getItem(position);

        viewHolder.mPlaceName.setText(place.getName());
        viewHolder.mAddress.setText(place.getVicinity());
        String rating = place.getRating();

        if (!TextUtils.isEmpty(rating)) {
            viewHolder.mRatingBar.setRating(Float.valueOf(place.getRating()));
            viewHolder.mRatingText.setText(place.getRating());
        } else {
            viewHolder.mRatingBar.setRating(0F);
            viewHolder.mRatingText.setText(mContext.getString(R.string.not_rated));
        }

        viewHolder.mDistance.setText(Utility.distanceFromLatitudeLongitude(Double.valueOf(PreferenceManager.getLatitude()),
                Double.valueOf(PreferenceManager.getLongitude()),
                place.getLatitude(),
                place.getLongitude(),
                PreferenceManager.getDistanceFormat()));

        if (place.isOpenNow()) {
            viewHolder.mOpenNow.setText(R.string.open);
            viewHolder.mOpenNow.setTextColor(ColorStateList.valueOf(Color.RED));
        } else {
            viewHolder.mOpenNow.setText(R.string.closed);
            viewHolder.mOpenNow.setTextColor(ColorStateList.valueOf(Color.DKGRAY));
        }

        viewHolder.mPriceLevel.removeAllViews();
        for (int i = 0; i < place.getPriceLevel(); i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageResource(R.drawable.dollar);
            viewHolder.mPriceLevel.addView(imageView);
        }

        if (place.getPhotoReference() != null) {
            viewHolder.mPlacePhoto.setImageUrl(Utility.getPlacePhotoQuery(place.getPhotoReference().getReference(),
                            (int) Utility.getDpToPixel(mContext, 80),
                            (int) Utility.getDpToPixel(mContext, 80)),
                    mImageLoader);
        }

        return convertView;
    }

    private static class ViewHolder {
        public ImageView mIcon;
        public NetworkImageView mPlacePhoto;
        public TextView mPlaceName;
        public TextView mAddress;
        public TextView mOpenNow;
        public AppCompatRatingBar mRatingBar;
        public TextView mRatingText;
        public TextView mDistance;
        public LinearLayout mPriceLevel;
    }
}