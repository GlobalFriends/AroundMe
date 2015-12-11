package com.globalfriends.com.aroundme.ui.review;

/**
 * Created by Vishal on 12/10/2015.
 */

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.utils.Utility;

import java.util.List;

/**
 * Review list adapter
 */
class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<PlaceReviewMetadata> mContent;
    private Context mContext;
    private ImageLoader mImageLoader;

    public ReviewAdapter(Context context, ImageLoader imageLoader, List<PlaceReviewMetadata> items) {
        this.mContent = items;
        mImageLoader = imageLoader;
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

        holder.mAvatar.setImageUrl(content.getmProfilePhotoUrl(), mImageLoader);
        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
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
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        protected AppCompatRatingBar mRatingBar;
        protected AppCompatTextView mRatingText;
        protected NetworkImageView mAvatar;
        protected AppCompatTextView mReviewContent;
        protected AppCompatTextView mReviewTiming;
        protected AppCompatTextView mAuthorName;

        public ReviewViewHolder(View view) {
            super(view);
            mRatingBar = (AppCompatRatingBar) view.findViewById(R.id.review_rating_bar);
            mRatingText = (AppCompatTextView) view.findViewById(R.id.review_rating_text);
            mReviewTiming = (AppCompatTextView) view.findViewById(R.id.review_rating_time);
            mAuthorName = (AppCompatTextView) view.findViewById(R.id.review_author_name);
            mAvatar = (NetworkImageView) view.findViewById(R.id.review_avatar);
            mReviewContent = (AppCompatTextView) view.findViewById(R.id.review_comment);
        }
    }
}
