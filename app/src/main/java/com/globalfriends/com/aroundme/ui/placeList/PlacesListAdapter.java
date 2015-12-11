package com.globalfriends.com.aroundme.ui.placeList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.photo = (NetworkImageView) convertView.findViewById(R.id.photo);
            viewHolder.placeName = (TextView) convertView.findViewById(R.id.place_name);
            viewHolder.vicinity = (TextView) convertView.findViewById(R.id.vicinity);
            viewHolder.openNow = (TextView) convertView.findViewById(R.id.open_now);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.rating);
            viewHolder.priceLevel = (LinearLayout) convertView.findViewById(R.id.price_level);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PlaceInfo place = getItem(position);

        viewHolder.placeName.setText(place.getName());
        viewHolder.vicinity.setText(place.getVicinity());
        viewHolder.rating.setText(place.getRating() == null ? mContext.getString(R.string.not_rated) : mContext.getString(R.string.rating, place.getRating()));
        viewHolder.openNow.setText(place.isOpenNow() ? R.string.open : R.string.closed);
        viewHolder.openNow.setTextColor(place.isOpenNow() ? ColorStateList.valueOf(Color.GREEN) : ColorStateList.valueOf(Color.LTGRAY));
        viewHolder.priceLevel.removeAllViews();
        for (int i = 0; i < place.getPriceLevel(); i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageResource(R.drawable.dollar);
            viewHolder.priceLevel.addView(imageView);
        }

        if (place.getPhotoReference() != null) {
            viewHolder.photo.setImageUrl(Utility.getPlacePhotoQuery(place.getPhotoReference().getReference(),
                            (int) Utility.getDpToPixel(mContext, 80),
                            (int) Utility.getDpToPixel(mContext, 80)),
                    mImageLoader);
        }

        return convertView;
    }

    private static class ViewHolder {
        public ImageView icon;
        public NetworkImageView photo;
        public TextView placeName;
        public TextView vicinity;
        public TextView openNow;
        public TextView rating;
        public LinearLayout priceLevel;
    }
}