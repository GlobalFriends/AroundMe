package com.globalfriends.com.aroundme.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vishal on 12/6/2015.
 */
public class PlaceReviewMetadata implements Parcelable {
    private String mRating;
    private String mLanguage;
    private String mAuthorName;
    private String mAuthorUrl;
    private String mProfilePhotoUrl;
    private String mReviewContent;

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
                ", mReviewContent='" + mReviewContent + '\'' +
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

    public String getReviewText() {
        return mReviewContent;
    }

    public void setmReviewContent(String mReviewContent) {
        this.mReviewContent = mReviewContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mRating);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mAuthorName);
        dest.writeString(this.mAuthorUrl);
        dest.writeString(this.mProfilePhotoUrl);
        dest.writeString(this.mReviewContent);
        dest.writeValue(this.mReviewTime);
    }

    public PlaceReviewMetadata() {
    }

    protected PlaceReviewMetadata(Parcel in) {
        this.mRating = in.readString();
        this.mLanguage = in.readString();
        this.mAuthorName = in.readString();
        this.mAuthorUrl = in.readString();
        this.mProfilePhotoUrl = in.readString();
        this.mReviewContent = in.readString();
        this.mReviewTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<PlaceReviewMetadata> CREATOR = new Parcelable.Creator<PlaceReviewMetadata>() {
        public PlaceReviewMetadata createFromParcel(Parcel source) {
            return new PlaceReviewMetadata(source);
        }

        public PlaceReviewMetadata[] newArray(int size) {
            return new PlaceReviewMetadata[size];
        }
    };
}

