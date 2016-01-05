package com.globalfriends.com.aroundme.data.places;

import com.globalfriends.com.aroundme.R;

/**
 * Created by Vishal on 12/13/2015.
 */
public enum ReviewAspect {
    Poor(R.string.review_poor), Average(R.string.review_average),
    Good(R.string.review_good), Excellent(R.string.review_excellent);
    private int mReviewDescription;

    ReviewAspect(final int reviewDescription) {
        mReviewDescription = reviewDescription;
    }

    public int getReviewString() {
        return mReviewDescription;
    }
}
