package com.globalfriends.com.aroundme.data;

/**
 * Created by Vishal on 12/6/2015.
 */
public class PlaceReviewMetadata {
    private String mRating;
    private String mLanguage;
    private String mAuthorName;
    private String mAuthorUrl;
    private String mProfilePhotoUrl;
    private String mReviewText;

    public String getmProfilePhotoUrl() {
        return mProfilePhotoUrl;
    }

    public void setmProfilePhotoUrl(String mProfilePhotoUrl) {
        this.mProfilePhotoUrl = mProfilePhotoUrl;
    }

    public Long getReviewTime() {
        return mReviewTime;
    }

    @Override
    public String toString() {
        return "PlaceReviewMetadata{" +
                "mRating='" + mRating + '\'' +
                ", mLanguage='" + mLanguage + '\'' +
                ", mAuthorName='" + mAuthorName + '\'' +
                ", mAuthorUrl='" + mAuthorUrl + '\'' +
                ", mProfilePhotoUrl='" + mProfilePhotoUrl + '\'' +
                ", mReviewText='" + mReviewText + '\'' +
                ", mReviewTime=" + mReviewTime +
                '}';
    }

    public void setReviewTime(Long mReviewTime) {
        this.mReviewTime = mReviewTime;
    }

    private Long mReviewTime;

    public String getRating() {
        return mRating;
    }

    public void setRating(String mRating) {
        this.mRating = mRating;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String mAthorName) {
        this.mAuthorName = mAthorName;
    }

    public String getmAuthorUrl() {
        return mAuthorUrl;
    }

    public void setmAuthorUrl(String mAuthorUrl) {
        this.mAuthorUrl = mAuthorUrl;
    }

    public String getmReviewText() {
        return mReviewText;
    }

    public void setmReviewText(String mReviewText) {
        this.mReviewText = mReviewText;
    }
}

