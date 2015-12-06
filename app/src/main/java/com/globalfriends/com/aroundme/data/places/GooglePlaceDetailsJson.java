package com.globalfriends.com.aroundme.data.places;

import com.globalfriends.com.aroundme.data.DefaultPlaceDetails;
import com.globalfriends.com.aroundme.data.PlacePhotoMetadata;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 */
public class GooglePlaceDetailsJson extends DefaultPlaceDetails {
    public GooglePlaceDetailsJson(JSONObject response) {
        super(response);
        JsonTOPointToReference(mResponse);
    }

    private GooglePlaceDetailsJson JsonTOPointToReference(JSONObject response) {
        try {
            JSONObject geometry = (JSONObject) response.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            mLatitude = ((Double) location.get("lat"));
            mLongitude = ((Double) location.get("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mAddress = response.getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mPhoneNumber = response.getString("international_phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mName = response.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            mWebUrl = response.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject openingHours = null;
        try {
            openingHours = (JSONObject) response.get("opening_hours");
            mOpneNow = openingHours.getBoolean("open_now");
            JSONArray weeklyText = openingHours.getJSONArray("weekday_text");
            if (weeklyText != null) {
                for (int i = 0; i < weeklyText.length(); i++) {
                    String timing = weeklyText.get(i).toString();
                    mWeeklyTimings.add(timing.replace("\"", ""));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonPhotoArray = (JSONArray) response.getJSONArray("photos");
            if (jsonPhotoArray != null) {
                for (int i = 0; i < jsonPhotoArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonPhotoArray.get(i);
                    PlacePhotoMetadata photo = new PlacePhotoMetadata();
                    photo.setReference(obj.getString("photo_reference"));
                    photo.setHeight(obj.getString("height"));
                    photo.setWidth(obj.getString("width"));
                    updatePhotoToList(photo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            JSONArray jsonReviewArray = (JSONArray) response.getJSONArray("reviews");
            if (jsonReviewArray != null) {
                for (int i = 0; i < jsonReviewArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonReviewArray.get(i);
                    PlaceReviewMetadata review = new PlaceReviewMetadata();
                    review.setAuthorName(obj.getString("author_name"));
                    review.setmAuthorUrl(obj.getString("author_url"));
                    review.setLanguage(obj.getString("language"));
                    review.setRating(obj.getString("rating"));
                    review.setReviewTime(obj.getLong("rating"));
                    try {
                        review.setmProfilePhotoUrl(obj.getString("profile_photo_url"));
                    } catch (JSONException e) {
                    }
                    updateReviewToList(review);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
}
