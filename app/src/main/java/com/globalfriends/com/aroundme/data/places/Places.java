package com.globalfriends.com.aroundme.data.places;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Model class for Places data.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 */
public class Places implements Parcelable {
    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;
    private String mPlace_id;
    private boolean mOpenNow;

    public static class PhotoRef {
        private int mHeight;
        private int mWidth;
        private String mReference;

        public int getHeight() {
            return mHeight;
        }

        public void setHeight(int height) {
            mHeight = height;
        }

        public int getWidth() {
            return mWidth;
        }

        public void setWidth(int width) {
            mWidth = width;
        }

        public String getReference() {
            return mReference;
        }

        public void setReference(String reference) {
            mReference = reference;
        }
    }

    private PhotoRef mPhotoRef;

    public static Places jsonToPontoReferencia(JSONObject pontoReferencia) {
        try {
            Places result = new Places();
            JSONObject geometry = (JSONObject) pontoReferencia.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(pontoReferencia.getString("icon"));
            result.setName(pontoReferencia.getString("name"));
            result.setVicinity(pontoReferencia.getString("vicinity"));
            result.setId(pontoReferencia.getString("id"));
            result.setPlace_id(pontoReferencia.getString("place_id"));

            if (pontoReferencia.has("photos")) {
                JSONArray photosArray = pontoReferencia.getJSONArray("photos");

                Places.PhotoRef photoRef = new PhotoRef();
                photoRef.setHeight(((JSONObject) photosArray.get(0)).getInt("height"));
                photoRef.setWidth(((JSONObject)photosArray.get(0)).getInt("width"));
                photoRef.setReference(((JSONObject)photosArray.get(0)).getString("photo_reference"));
                result.setPhotoReference(photoRef);
            }

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Places.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setPlace_id(String id) {
        this.mPlace_id = id;
    }

    public String getPlaceId() {
        return this.mPlace_id;
    }

    public PhotoRef getPhotoReference() {
        return mPhotoRef;
    }

    public void setPhotoReference(PhotoRef reference) {
        mPhotoRef = reference;
    }

    public String getPhoto(int maxWidth, String key) {
        if (mPhotoRef == null) {
            return null;
        }
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + maxWidth + "&photoreference=" + mPhotoRef.getReference() + "&key=" + key;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude
                + ", longitude=" + longitude + " mPlace_id=" + mPlace_id +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.vicinity);
    }
}