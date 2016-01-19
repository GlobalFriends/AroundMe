package com.globalfriends.com.aroundme.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhotoViewer extends AppCompatActivity {
    private List<PlacePhotoMetadata> mPhotoList = new ArrayList<>();
    private PlacePhotoMetadata mCurrentPhoto;
    private ImageLoader mImageLoader;
    private String mImageLoaderKey;
    private int mCurrentPosition;
    private ViewPager mViewPager;
    private CustomPagerAdapter mCustomPagerAdapter;

    class CustomPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.activity_photo_viewer, container, false);

            NetworkImageView imageView = (NetworkImageView) itemView.findViewById(R.id.image);
            PlacePhotoMetadata metaData = mPhotoList.get(position);
            imageView.setImageUrl(Utility.getPlacePhotoQuery(metaData.getReference(),
                            Integer.valueOf(metaData.getHeight()),
                            Integer.valueOf(metaData.getWidth())),
                    mImageLoader);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mPhotoList = (List<PlacePhotoMetadata>) intent.getSerializableExtra("PHOTO_LIST");
            mCurrentPhoto = (PlacePhotoMetadata) intent.getParcelableExtra("CURRENT_PHOTO");
            mImageLoaderKey = intent.getStringExtra("KEY");
        }

        if (savedInstanceState != null) {
            mPhotoList = (List<PlacePhotoMetadata>) savedInstanceState.getSerializable("PHOTO_LIST");
            mCurrentPhoto = (PlacePhotoMetadata) savedInstanceState.getParcelable("CURRENT_PHOTO");
            mImageLoaderKey = savedInstanceState.getString("KEY");
            mCurrentPosition = savedInstanceState.getInt("POSITION");
        }

        for (int i = 0; i < mPhotoList.size(); i++) {
            PlacePhotoMetadata data = mPhotoList.get(i);
            if (!TextUtils.isEmpty(data.getReference())) {
                if (data.getReference().equalsIgnoreCase(mCurrentPhoto.getReference())) {
                    mCurrentPosition = i;
                    break;
                }
            } else if (!TextUtils.isEmpty(data.getUrl())) {
                if (data.getUrl().equalsIgnoreCase(mCurrentPhoto.getUrl())) {
                    mCurrentPosition = i;
                    break;
                }
            }
        }

        mImageLoader = TransactionManager.getInstance().getModuleImageLoader(mImageLoaderKey);
        setContentView(R.layout.activity_page_viewer);
        mCustomPagerAdapter = new CustomPagerAdapter(this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("PHOTO_LIST", (Serializable) mPhotoList);
        outState.putParcelable("CURRENT_PHOTO", mCurrentPhoto);
        outState.putString("KEY", mImageLoaderKey);
        outState.putInt("POSITION", mCurrentPosition);
        super.onSaveInstanceState(outState);
    }
}
