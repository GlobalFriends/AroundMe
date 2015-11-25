package com.globalfriends.com.aroundme.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.places.Places;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by vishal on 11/14/2015.
 */
public class RecentFragment extends Fragment implements AbsListView.OnItemClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Created by anup on 11/10/15.
     */
    public static class PlacesListAdapter extends ArrayAdapter<Places> {
        Context mContext;
        Places[] mPlaces;

        public PlacesListAdapter(Context context, Places[] objects) {
            super(context, R.layout.layout_places_item, objects);
            mContext = context;
            mPlaces = objects;

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300)).build();

            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getContext())
                    .defaultDisplayImageOptions(options)
                    .memoryCache(new WeakMemoryCache())
                    .discCacheSize(100 * 1024 * 1024).build();

            ImageLoader.getInstance().init(configuration);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.layout_places_item, null);

                viewHolder = new ViewHolder();
                viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
                viewHolder.placeName = (TextView) convertView.findViewById(R.id.place_name);
                viewHolder.vicinity = (TextView) convertView.findViewById(R.id.vincinity);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Places place = getItem(position);

            viewHolder.placeName.setText(place.getName());
            viewHolder.vicinity.setText(place.getVicinity());
            if (place.getPhotoReference() != null) {
                ImageLoader.getInstance().displayImage(place.getPhoto(200, getContext().getResources().getString(R.string.google_maps_key)), viewHolder.photo);
            }

            return convertView;
        }

        /*
        @Override
        public int getCount() {
            return mPlaces.length;
        }

        @Override
        public Object getItem(int position) {
            return mPlaces[position];
        }

        @Override
        public long getItemId(int position) {

            return position;
        }*/

        private static class ViewHolder {
            public ImageView photo;
            public TextView placeName;
            public TextView vicinity;
        }
    }
}
