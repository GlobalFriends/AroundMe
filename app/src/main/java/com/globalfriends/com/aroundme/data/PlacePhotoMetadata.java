package com.globalfriends.com.aroundme.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vishal on 12/6/2015.
 */
public class PlacePhotoMetadata implements Parcelable {
    public static final Creator<PlacePhotoMetadata> CREATOR = new Creator<PlacePhotoMetadata>() {
        public PlacePhotoMetadata createFromParcel(Parcel source) {
            return new PlacePhotoMetadata(source);
        }

        public PlacePhotoMetadata[] newArray(int size) {
            return new PlacePhotoMetadata[size];
        }
    };
    private int mHeight;
    private int mWidth;
    private String mPhotoReference;
    private String mPhotoUrl;
    private String url;

    public PlacePhotoMetadata() {
    }

    private PlacePhotoMetadata(Parcel in) {
        this.mHeight = in.readInt();
        this.mWidth = in.readInt();
        this.mPhotoReference = in.readString();
        this.mPhotoUrl = in.readString();
        this.url = in.readString();
    }

    @Override
    public String toString() {
        return "PlacePhotoMetadata{" +
                "mHeight=" + mHeight +
                ", mWidth=" + mWidth +
                ", mPhotoReference='" + mPhotoReference + '\'' +
                ", mPhotoUrl='" + mPhotoUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getUrl() {
        return mPhotoUrl;
    }

    public void setUrl(String url) {
        this.mPhotoUrl = url;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public String getReference() {
        return mPhotoReference;
    }

    public void setReference(String mReference) {
        this.mPhotoReference = mReference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mWidth);
        dest.writeString(this.mPhotoReference);
        dest.writeString(this.mPhotoUrl);
        dest.writeString(this.url);
    }
}
