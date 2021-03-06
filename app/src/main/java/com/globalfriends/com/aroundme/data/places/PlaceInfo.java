package com.globalfriends.com.aroundme.data.places;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Model class for data.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 */
public class PlaceInfo implements Parcelable {
    public static final Creator<PlaceInfo> CREATOR = new Creator<PlaceInfo>() {
        public PlaceInfo createFromParcel(Parcel source) {
            return new PlaceInfo(source);
        }

        public PlaceInfo[] newArray(int size) {
            return new PlaceInfo[size];
        }
    };
    private String id;
    private String icon;
    private String name;
    private String vicinity;
    private Double latitude;
    private Double longitude;
    private String mPlace_id;
    private PlacePhotoMetadata mPhoto;
    private String mRating;
    private boolean mOpenNow;
    private boolean mOpenClosePresent;
    private boolean mPermanentlyClosed;
    private int mPriceLevel;

    public PlaceInfo() {
    }

    private PlaceInfo(Parcel in) {
        this.id = in.readString();
        this.icon = in.readString();
        this.name = in.readString();
        this.vicinity = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.mPlace_id = in.readString();
        this.mPhoto = in.readParcelable(PlacePhotoMetadata.class.getClassLoader());
        this.mRating = in.readString();
        this.mOpenNow = in.readByte() != 0;
        this.mPriceLevel = in.readInt();
    }

    public static PlaceInfo jsonToPontoReferencia(JSONObject pontoReferencia) {
        try {
            PlaceInfo result = new PlaceInfo();
            JSONObject geometry = (JSONObject) pontoReferencia.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(pontoReferencia.getString("icon"));
            result.setName(pontoReferencia.getString("name"));

            if (pontoReferencia.has("vicinity")) {
                result.setVicinity(pontoReferencia.getString("vicinity"));
            }

            if (pontoReferencia.has("permanently_closed")) {
                try {
                    result.setPermanentlyClosed(pontoReferencia.getBoolean("permanently_closed"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (pontoReferencia.has("formatted_address")) {
                result.setVicinity(pontoReferencia.getString("formatted_address"));
            }

            if (pontoReferencia.has("rating")) {
                result.setRating(pontoReferencia.getString("rating"));
            }

            if (pontoReferencia.has("price_level")) {
                result.setPriceLevel(pontoReferencia.getInt("price_level"));
            }

            JSONObject openingHours;
            if (pontoReferencia.has("opening_hours")) {
                result.setIsOpenClosePresent(true);
                openingHours = (JSONObject) pontoReferencia.get("opening_hours");
                if (openingHours.has("open_now")) {
                    result.setOpenNow(openingHours.getBoolean("open_now"));
                }
            }

            result.setId(pontoReferencia.getString("id"));
            result.setPlace_id(pontoReferencia.getString("place_id"));

            if (pontoReferencia.has("photos")) {
                PlacePhotoMetadata photo = new PlacePhotoMetadata();
                JSONArray photosArray = pontoReferencia.getJSONArray("photos");
                photo.setHeight(((JSONObject) photosArray.get(0)).getInt("height"));
                photo.setWidth(((JSONObject) photosArray.get(0)).getInt("width"));
                photo.setReference(((JSONObject) photosArray.get(0)).getString("photo_reference"));
                result.setPhotoMetadata(photo);
            }

            return result;
        } catch (JSONException ex) {
            Logger.getLogger(PlaceInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setPhotoMetadata(PlacePhotoMetadata photo) {
        this.mPhoto = photo;
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

    public void setPermanentlyClosed(boolean permanentlyClosed) {
        this.mPermanentlyClosed = permanentlyClosed;
    }

    public boolean getPermanentlyClosed() {
        return mPermanentlyClosed;
    }

    public void setPlace_id(String id) {
        this.mPlace_id = id;
    }

    public String getPlaceId() {
        return this.mPlace_id;
    }

    public PlacePhotoMetadata getPhotoReference() {
        return mPhoto;
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude
                + ", longitude=" + longitude + " mPlace_id=" + mPlace_id +
                '}';
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        this.mRating = rating;
    }

    public boolean isOpenNow() {
        return mOpenNow;
    }

    public void setOpenNow(boolean openNow) {
        this.mOpenNow = openNow;
    }

    public void setIsOpenClosePresent(boolean present) {
        this.mOpenClosePresent = present;
    }

    public boolean getOpenClosePresent() {
        return mOpenClosePresent;
    }

    public int getPriceLevel() {
        return mPriceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.mPriceLevel = priceLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.icon);
        dest.writeString(this.name);
        dest.writeString(this.vicinity);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeString(this.mPlace_id);
        dest.writeParcelable(this.mPhoto, 0);
        dest.writeString(this.mRating);
        dest.writeByte(mOpenNow ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mPriceLevel);
    }
}