package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.Places;
import com.globalfriends.com.aroundme.logging.Logger;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

import testing.PlacesService;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListFragment extends ListFragment {
    private static final String TAG = "PlacesListFragment";
    private ArrayList<Places> mPlaces = new ArrayList<>();
    private OnPlaceListFragmentSelection mListener;
    private PlacesListAdapter mAdapter;
    private ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setMessage(getResources().getString(R.string.please_wait_progress));
        mProgress.isIndeterminate();
        mProgress.show();
        new GetPlaces(getArguments().getString("PLACE_EXTRA")).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new PlacesListAdapter(getActivity(), mPlaces);
        setListAdapter(mAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.search_results);
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        if (null != mListener) {
            Places details = (Places) parent.getItemAtPosition(position);
            Log.i(TAG, ">>>> " + details.toString());
            mListener.OnPlaceListFragmentSelection(details.getPlaceId(), "");
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Logger.i(TAG, "onAttach");
        try {
            mListener = (OnPlaceListFragmentSelection) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
    }

    public interface OnPlaceListFragmentSelection {
        void OnPlaceListFragmentSelection(final String placeId, final String phone);
    }

    /**
     * Created by anup on 11/10/15.
     */
    public static class PlacesListAdapter extends ArrayAdapter<Places> {
        Context mContext;
        ArrayList<Places> mPlaces;

        public PlacesListAdapter(Context context, ArrayList<Places> objects) {
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

        public void swapItem(ArrayList<Places> object) {
            mPlaces.addAll(object);
            notifyDataSetChanged();
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
                ImageLoader.getInstance().displayImage(place.getPhoto(200,
                        getContext().getResources().getString(R.string.google_maps_key)), viewHolder.photo);
            }

            return convertView;
        }

        private static class ViewHolder {
            public ImageView photo;
            public TextView placeName;
            public TextView vicinity;
        }
    }

    /**
     * Find places based on Latitude and Longitude
     */
    private class GetPlaces extends AsyncTask<Void, Void, Void> {
        private String places;

        public GetPlaces(String places) {
            this.places = places;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }

            if (mPlaces == null || mPlaces.size() == 0) {
                Logger.i(TAG, "No results found");
                //TODO: Update List fragment with no results
                return;
            }
            mAdapter.swapItem(mPlaces);
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    getResources().getString(R.string.google_maps_key));
            mPlaces = service.findPlaces(PreferenceManager.getLocation(),
                    TextUtils.isEmpty(places) ? "atm" : places); //0 77.218276
            return null;
        }
    }
}
