package com.globalfriends.com.aroundme.ui.review;

/**
 * Created by Vishal on 12/10/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;
import com.globalfriends.com.aroundme.ui.CircularNetworkImageView;
import com.globalfriends.com.aroundme.utils.Utility;

import java.util.List;

/**
 * Review list adapter
 */
class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final Context mContext;
    private List<PlaceReviewMetadata> mContent;
    private ImageLoader mImageLoader;
    private Bitmap mProfileBitmap;
    private Bitmap mDefaultProfileBitmap;

    public ReviewAdapter(Activity context, ImageLoader imageLoader, List<PlaceReviewMetadata> items) {
        this.mContent = items;
        mImageLoader = imageLoader;
        mContext = context;
        mProfileBitmap = Utility.getCircularBitmap(BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.profile));
        mDefaultProfileBitmap = Utility.getCircularBitmap(BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.ic_default_profile));
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

        final PlaceReviewMetadata content = mContent.get(position);
        if (content == null) {
            return; // Should never be the case
        }
        holder.mAvatar.setImageBitmap(mDefaultProfileBitmap);
        // Lets do a image first so that it gets some time to load
        if (content.getProfilePhotoUrl() != null) {
            holder.mAvatar.setImageUrl(content.getProfilePhotoUrl(), mImageLoader);
        } else {
            holder.mAvatar.setImageBitmap(mProfileBitmap);
        }

        if (!TextUtils.isEmpty(content.getRating())) {
            holder.mRatingText.setVisibility(View.VISIBLE);
            holder.mRatingBar.setVisibility(View.VISIBLE);
            holder.mRatingBar.setRating(Float.valueOf(content.getRating()));
            holder.mRatingText.setText(content.getRating());
        } else {
            holder.mRatingText.setVisibility(View.GONE);
            holder.mRatingBar.setVisibility(View.GONE);
        }

        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getAuthorUrl() == null) {
                    return;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getAuthorUrl()));
                mContext.startActivity(browserIntent);
            }
        });

        if (!TextUtils.isEmpty(content.getReviewText())) {
            holder.mReviewContent.setText(content.getReviewText());
        }

        String displayTime = Utility.Epoch2DateString(content.getReviewTime());
        if (!TextUtils.isEmpty(displayTime)) {
            holder.mReviewTiming.setText(displayTime);
        }

        if (!TextUtils.isEmpty(content.getAuthorName())) {
            holder.mAuthorName.setText(content.getAuthorName());
        }

        if (!TextUtils.isEmpty(content.getAspect())) {
            holder.mAspectType.setVisibility(View.VISIBLE);
            holder.mSeparator.setVisibility(View.VISIBLE);
            holder.mAspectType.setText(content.getAspect());
        } else {
            holder.mAspectType.setVisibility(View.GONE);
            holder.mSeparator.setVisibility(View.GONE);
        }

        if (content.getAspectDescription() != null) {
            holder.mAspectRating.setVisibility(View.VISIBLE);
            holder.mAspectRating.setText(content.getAspectDescription().toString());
        } else {
            holder.mAspectRating.setVisibility(View.GONE);
        }
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
        protected CircularNetworkImageView mAvatar;
        protected AppCompatTextView mReviewContent;
        protected AppCompatTextView mReviewTiming;
        protected AppCompatTextView mAuthorName;
        protected AppCompatTextView mAspectType;
        protected AppCompatTextView mAspectRating;
        protected AppCompatTextView mSeparator;

        public ReviewViewHolder(View view) {
            super(view);
            mRatingBar = (AppCompatRatingBar) view.findViewById(R.id.review_rating_bar);
            mRatingText = (AppCompatTextView) view.findViewById(R.id.review_rating_text);
            mReviewTiming = (AppCompatTextView) view.findViewById(R.id.review_rating_time);
            mAuthorName = (AppCompatTextView) view.findViewById(R.id.review_author_name);
            mAvatar = (CircularNetworkImageView) view.findViewById(R.id.review_avatar);
            mReviewContent = (AppCompatTextView) view.findViewById(R.id.review_comment);
            mSeparator = (AppCompatTextView) view.findViewById(R.id.seperator);
            mAspectType = (AppCompatTextView) view.findViewById(R.id.aspect_type_id);
            mAspectRating = (AppCompatTextView) view.findViewById(R.id.aspect_rating_id);
        }
    }
}
