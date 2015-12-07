package com.globalfriends.com.aroundme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.utils.Utility;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhotoViewer extends AppCompatActivity {
    private NetworkImageView mImageView;
    private List<PlacePhotoMetadata> mPhotoList;
    private PlacePhotoMetadata mCurrentPhoto;
    private ImageLoader mImageLoader;
    private String mImageLoaderKey;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

//    GestureDetectorCompat gesture = new GestureDetectorCompat();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mPhotoList = (List<PlacePhotoMetadata>) intent.getParcelableExtra("PHOTO_LIST");
            mCurrentPhoto = (PlacePhotoMetadata) intent.getParcelableExtra("CURRENT_PHOTO");
            mImageLoaderKey = intent.getStringExtra("KEY");
        }

        if (savedInstanceState != null) {
            mPhotoList = (List<PlacePhotoMetadata>) savedInstanceState.getParcelable("PHOTO_LIST");
            mCurrentPhoto = (PlacePhotoMetadata) savedInstanceState.getParcelable("CURRENT_PHOTO");
            mImageLoaderKey = savedInstanceState.getString("KEY");
        }
        mImageLoader = TransactionManager.getInstance().getModuleImageLoader(mImageLoaderKey);
        setContentView(R.layout.activity_photo_viewer);
        initView();
    }

    private void initView() {
        mImageView = (NetworkImageView) findViewById(R.id.image);
        if (mCurrentPhoto != null) {
            mImageView.setImageUrl(
                    Utility.getPlacePhotoQuery(mCurrentPhoto.getReference(),
                            Integer.valueOf(mCurrentPhoto.getHeight()),
                            Integer.valueOf(mCurrentPhoto.getWidth())),
                    mImageLoader);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("PHOTO_LIST", (Parcelable) mPhotoList);
        outState.putParcelable("CURRENT_PHOTO", mCurrentPhoto);
        outState.putString("KEY", mImageLoaderKey);
    }
}
