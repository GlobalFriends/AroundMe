package com.globalfriends.com.aroundme.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.utils.Utility;

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
        mAdapter = new ReviewAdapter(this, mReviewList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("REVIEW_LIST", (Serializable) mReviewList);
        outState.putString("TAG_NAME", mModuleTag);
        super.onSaveInstanceState(outState);
    }

    /**
     * Review list adapter
     */
    static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
        private List<PlaceReviewMetadata> mContent;
        private Context mContext;

        public ReviewAdapter(Context context, List<PlaceReviewMetadata> items) {
            this.mContent = items;
            mContext = context;
        }

        @Override
        public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, null);
            ReviewViewHolder viewHolder = new ReviewViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ReviewViewHolder holder, int position) {
            if (mContent == null || position > mContent.size()) {
                return;
            }

            PlaceReviewMetadata content = mContent.get(position);
            if (content == null) {
                return; // Should never be the case
            }
            holder.mRatingBar.setRating(Float.valueOf(content.getRating()));
            holder.mRatingText.setText(content.getRating());

            holder.mRatingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hnalde on click on this image.. Show avatar URL
                }
            });
            holder.mReviewContent.setText(content.getReviewText());
            holder.mReviewTiming.setText(Utility.getDate(content.getReviewTime()));
            holder.mAuthorName.setText(content.getAuthorName());
        }

        @Override
        public int getItemCount() {
            return (null != mContent ? mContent.size() : 0);
        }

        /**
         * View Holder for review list
         */
        private class ReviewViewHolder extends RecyclerView.ViewHolder {
            protected AppCompatRatingBar mRatingBar;
            protected AppCompatTextView mRatingText;
            protected NetworkImageView mAvatar;
            protected AppCompatTextView mReviewContent;
            protected AppCompatTextView mReviewTiming;
            protected AppCompatTextView mAuthorName;

            public ReviewViewHolder(View view) {
                super(view);
                mRatingBar = (AppCompatRatingBar) view.findViewById(R.id.rating);
                mRatingText = (AppCompatTextView) view.findViewById(R.id.rating_text);
                mAvatar = (NetworkImageView) view.findViewById(R.id.avatar);
                mReviewContent = (AppCompatTextView) view.findViewById(R.id.review_content);
                mReviewTiming = (AppCompatTextView) view.findViewById(R.id.rating_time);
                mAuthorName = (AppCompatTextView) view.findViewById(R.id.author_name);
            }
        }
    }
};