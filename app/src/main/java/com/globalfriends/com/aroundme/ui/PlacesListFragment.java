package com.globalfriends.com.aroundme.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.AroundMeApplication;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anup on 11/10/15.
 */
public class PlacesListFragment extends ListFragment {
    private static final String TAG = "PlacesListFragment";
    private List<PlaceInfo> mPlaces = new ArrayList<>();
    private OnPlaceListFragmentSelection mListener;
    private PlacesListAdapter mAdapter;
    private ProgressDialog mProgress;
    private ResultCallback mCallBack = new ResultCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setMessage(getResources().getString(R.string.please_wait_progress));
        mProgress.isIndeterminate();
        mProgress.show();
        TransactionManager.getInstance().addResultCallback(mCallBack);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAdapter = new PlacesListAdapter(getActivity(), mPlaces);
        setListAdapter(mAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TransactionManager.getInstance().findByNearBy(getArguments().getString("PLACE_EXTRA"));
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
            PlaceInfo details = (PlaceInfo) parent.getItemAtPosition(position);
            Log.i(TAG, ">>>> " + details.toString());
            mListener.OnPlaceListFragmentSelection(details);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPlaceListFragmentSelection) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectionFragmentSelection");
        }
    }

    /**
     * Handle selection from this fragment to main acitivty
     */
    public interface OnPlaceListFragmentSelection {
        void OnPlaceListFragmentSelection(final PlaceInfo place);

        void handleFragmentSuicidal(final String tag);
    }

    /**
     * Created by anup on 11/10/15.
     */
    public static class PlacesListAdapter extends ArrayAdapter<PlaceInfo> {
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
                                viewHolder.photo.getHeight() != 0 ? viewHolder.photo.getHeight() : (int) Utility.getDpToPixel(mContext, 80),
                                viewHolder.photo.getWidth() != 0 ? viewHolder.photo.getWidth() : (int) Utility.getDpToPixel(mContext, 80)),
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

    class ResultCallback extends TransactionManager.Result {
        @Override
        public void onPlacesList(List<PlaceInfo> placeList) {
            Log.i(TAG, "onPlacesList ....");
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mAdapter.swapItem(placeList);
        }

        @Override
        public void onError(final String errorMsg, final String tag) {
            if (!TextUtils.isEmpty(tag) && !tag.equalsIgnoreCase(getActivity().getResources().getString(R.string.google_places_tag))) {
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
