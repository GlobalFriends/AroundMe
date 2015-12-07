package com.globalfriends.com.aroundme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

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
    private static final String TAG = "PhotoViewer";
    private NetworkImageView mImageView;
    private List<PlacePhotoMetadata> mPhotoList = new ArrayList<>();
    private PlacePhotoMetadata mCurrentPhoto;
    private ImageLoader mImageLoader;
    private String mImageLoaderKey;
    private int mCurrentPosition;
    final GestureDetector gestureControl = new GestureDetector(new GestureListener());

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i(TAG, "Right to left");
                if ((mCurrentPosition + 1) <= mPhotoList.size()) {
                    movePhoto(mCurrentPosition + 1);
                }
                return false; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.i(TAG, "Left to right");
                if ((mCurrentPosition - 1) >= 0) {
                    movePhoto(mCurrentPosition - 1);
                }
                return false; // Left to right
            }
            return false;
        }
    }

    /**
     * @param position
     */
    private void movePhoto(final int position) {
        if (position < 0 || position >= mPhotoList.size()) {
            Log.e(TAG, "Wrong position. Something wring with caller position=" + position);
            return;
        }

        mCurrentPhoto = mPhotoList.get(position);
        mCurrentPosition = position;
        setupImage();
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
        setContentView(R.layout.activity_photo_viewer);

        mImageView = (NetworkImageView) findViewById(R.id.image);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureControl.onTouchEvent(event);
                return true;
            }
        });

        setupImage();
    }

    private void setupImage() {
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
        outState.putSerializable("PHOTO_LIST", (Serializable) mPhotoList);
        outState.putParcelable("CURRENT_PHOTO", mCurrentPhoto);
        outState.putString("KEY", mImageLoaderKey);
        outState.putInt("POSITION", mCurrentPosition);
    }
}
