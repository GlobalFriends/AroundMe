package com.globalfriends.com.aroundme.data.places;

import android.telephony.PhoneNumberUtils;

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

        if (response.has("permanently_closed")) {
            try {
                mPermanentlyClosed = response.getBoolean("permanently_closed");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            if (response.has("international_phone_number")) {
                mPhoneNumber = response.getString("international_phone_number");
            } else if (response.has("formatted_phone_number")) {
                mPhoneNumber = response.getString("formatted_phone_number");
                mPhoneNumber = PhoneNumberUtils.stripSeparators(mPhoneNumber);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (response.has("name")) {
                mName = response.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if (response.has("url")) {
                mWebUrl = response.getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject openingHours = null;
        try {
            if (response.has("opening_hours")) {
                openingHours = (JSONObject) response.get("opening_hours");
            }

            if (response.has("open_now")) {
                mOpenNow = openingHours.getBoolean("open_now");
            }

            if (response.has("weekday_text")) {
                JSONArray weeklyText = openingHours.getJSONArray("weekday_text");
                if (weeklyText != null) {
                    for (int i = 0; i < weeklyText.length(); i++) {
                        String timing = weeklyText.get(i).toString();
                        mWeeklyTimings.add(timing.replace("\"", ""));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mIcon = response.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (response.has("photos")) {
            try {
                JSONArray jsonPhotoArray = (JSONArray) response.getJSONArray("photos");
                if (jsonPhotoArray != null) {
                    for (int i = 0; i < jsonPhotoArray.length(); i++) {
                        JSONObject obj = (JSONObject) jsonPhotoArray.get(i);
                        PlacePhotoMetadata photo = new PlacePhotoMetadata();
                        if (obj.has("photo_reference")) {
                            photo.setReference(obj.getString("photo_reference"));
                        }

                        if (obj.has("height")) {
                            photo.setHeight(obj.getInt("height"));
                        }

                        if (obj.has("width")) {
                            photo.setWidth(obj.getInt("width"));
                        }
                        updatePhotoToList(photo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            if (response.has("reviews")) {
                JSONArray jsonReviewArray = (JSONArray) response.getJSONArray("reviews");
                if (jsonReviewArray != null) {
                    mReviewCount = jsonReviewArray.length();
                    for (int i = 0; i < jsonReviewArray.length(); i++) {
                        JSONObject obj = (JSONObject) jsonReviewArray.get(i);
                        PlaceReviewMetadata review = new PlaceReviewMetadata();
                        if (obj.has("author_name")) {
                            review.setAuthorName(obj.getString("author_name"));
                        }

                        if (obj.has("author_url")) {
                            review.setAuthorUrl(obj.getString("author_url"));
                        }

                        if (obj.has("language")) {
                            review.setLanguage(obj.getString("language"));
                        }

                        if (obj.has("rating")) {
                            review.setRating(obj.getString("rating"));
                        }

                        if (obj.has("time")) {
                            review.setReviewTime(obj.getLong("time"));
                        }

                        if (obj.has("text")) {
                            review.setReviewContent(obj.getString("text"));
                        }

                        if (obj.has("profile_photo_url")) {
                            review.setProfilePhotoUrl(obj.getString("profile_photo_url"));
                        }

                        if (obj.has("aspects")) {
                            JSONArray aspectArray = (JSONArray) obj.getJSONArray("aspects");
                            if (aspectArray != null) {
                                for (int traverse = 0; traverse < aspectArray.length(); traverse++) {
                                    JSONObject ratingDetail = (JSONObject) aspectArray.get(traverse);
                                    review.setAspectDescription(ratingDetail.getInt("rating"));
                                    review.setAspect(ratingDetail.getString("type"));
                                }
                            }
                        }
                        updateReviewToList(review);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
}
