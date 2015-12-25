package com.globalfriends.com.aroundme.ui.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.protocol.TransactionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vishal on 12/8/2015.
 */
public class ReviewList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mModuleTag;
    private ImageLoader mImageLoader;
    private List<PlaceReviewMetadata> mReviewList = new ArrayList<PlaceReviewMetadata>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.reviews));
        setContentView(R.layout.review_details);

        Intent intent = getIntent();
        if (intent != null) {
            mReviewList = (List<PlaceReviewMetadata>) intent.getSerializableExtra("REVIEW_LIST");
            mModuleTag = intent.getStringExtra("TAG_NAME");
            if (!TextUtils.isEmpty(mModuleTag)) {
                mImageLoader = TransactionManager.getInstance().getModuleImageLoader(mModuleTag);
            }
        }

        if (savedInstanceState != null) {
            mReviewList = (List<PlaceReviewMetadata>) savedInstanceState.getSerializable("REVIEW_LIST");
            mModuleTag = savedInstanceState.getString("TAG_NAME");

            if (!TextUtils.isEmpty(mModuleTag)) {
                mImageLoader = TransactionManager.getInstance().getModuleImageLoader(mModuleTag);
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.review_list_id);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReviewAdapter(this, mImageLoader, mReviewList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("REVIEW_LIST", (Serializable) mReviewList);
        outState.putString("TAG_NAME", mModuleTag);
        super.onSaveInstanceState(outState);
    }
};