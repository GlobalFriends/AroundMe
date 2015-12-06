package com.globalfriends.com.aroundme.data;

/**
 * Created by Vishal on 12/6/2015.
 */
public class PlacePhotoMetadata {
    private String mHeight;
    private String mWidth;
    private String mPhotoReference;
    private String mPhotoUrl;

    public String getUrl() {
        return mPhotoUrl;
    }

    public void setUrl(String url) {
        this.mPhotoUrl = url;
    }

    private String url;

    public PlacePhotoMetadata() {
    }

    public String getHeight() {
        return mHeight;
    }

    public void setHeight(String mHeight) {
        this.mHeight = mHeight;
    }

    public String getWidth() {
        return mWidth;
    }

    public void setWidth(String mWidth) {
        this.mWidth = mWidth;
    }

    public String getReference() {
        return mPhotoReference;
    }

    public void setReference(String mReference) {
        this.mPhotoReference = mReference;
    }
}
