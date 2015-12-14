package com.globalfriends.com.aroundme.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.globalfriends.com.aroundme.data.places.ReviewAspect;

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
    private String mAspect;
    private ReviewAspect mAspectDesc;

    public String getAspect() {
        return mAspect;
    }

    public void setAspect(String mReviewAspect) {
        this.mAspect = mReviewAspect;
    }

    public ReviewAspect getAspectDescription() {
        return mAspectDesc;
    }

    public void setAspectDescription(int rating) {
        switch (rating) {
            case 0:
                mAspectDesc = ReviewAspect.Poor;
                break;
            case 1:
                mAspectDesc = ReviewAspect.Average;
                break;
            case 2:
                mAspectDesc = ReviewAspect.Good;
                break;
            case 3:
                mAspectDesc = ReviewAspect.Excellent;
                break;
            default:
                mAspectDesc = ReviewAspect.Poor;
        }
    }

    public String getProfilePhotoUrl() {
        if (mProfilePhotoUrl == null) {
            return mProfilePhotoUrl;
        }
        return "https:" + mProfilePhotoUrl;
    }

    public void setProfilePhotoUrl(String mProfilePhotoUrl) {
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

    public String getAuthorUrl() {
        return mAuthorUrl;
    }

    public void setAuthorUrl(String mAuthorUrl) {
        this.mAuthorUrl = mAuthorUrl;
    }

    public String getReviewText() {
        return mReviewContent;
    }

    public void setReviewContent(String mReviewContent) {
        this.mReviewContent = mReviewContent;
    }

    public PlaceReviewMetadata() {
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
        dest.writeString(this.mAspect);
        dest.writeInt(this.mAspectDesc == null ? -1 : this.mAspectDesc.ordinal());
        dest.writeValue(this.mReviewTime);
    }

    protected PlaceReviewMetadata(Parcel in) {
        this.mRating = in.readString();
        this.mLanguage = in.readString();
        this.mAuthorName = in.readString();
        this.mAuthorUrl = in.readString();
        this.mProfilePhotoUrl = in.readString();
        this.mReviewContent = in.readString();
        this.mAspect = in.readString();
        int tmpMAspectDesc = in.readInt();
        this.mAspectDesc = tmpMAspectDesc == -1 ? null : ReviewAspect.values()[tmpMAspectDesc];
        this.mReviewTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<PlaceReviewMetadata> CREATOR = new Creator<PlaceReviewMetadata>() {
        public PlaceReviewMetadata createFromParcel(Parcel source) {
            return new PlaceReviewMetadata(source);
        }

        public PlaceReviewMetadata[] newArray(int size) {
            return new PlaceReviewMetadata[size];
        }
    };
}

