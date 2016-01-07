package com.globalfriends.com.aroundme.data.fourSquare;

import android.text.TextUtils;

import com.globalfriends.com.aroundme.data.DefaultPlaceDetails;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vishal on 12/21/2015.
 */
public class FourSquarePlaceInfoJson extends DefaultPlaceDetails {

    public FourSquarePlaceInfoJson(JSONObject response) {
        super(response);
        JsonTOPointToReference(mResponse);
    }

    private FourSquarePlaceInfoJson JsonTOPointToReference(JSONObject response) {
        if (response.has("rating")) {
            try {
                String rating = response.getString("rating");
                mPlaceRating = Float.toString(Float.valueOf(rating) / 2F);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (response.has("ratingColor")) {
            try {
                mRatingColor = response.getString("ratingColor");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // We will start directly from reviews only..
        if (response.has("tips")) {
            try {
                JSONObject tips = response.getJSONObject("tips");
                if (tips.has("count")) {
                    mReviewCount = tips.getInt("count");
                }
                if (tips.has("groups")) {
                    JSONArray groupsArray = tips.getJSONArray("groups");
                    for (int i = 0; i < groupsArray.length(); i++) {
                        JSONObject obj = (JSONObject) groupsArray.get(i);
                        if (obj.has("items")) {
                            JSONArray itemsArray = obj.getJSONArray("items");
                            for (int j = 0; j < itemsArray.length(); j++) {
                                PlaceReviewMetadata review = new PlaceReviewMetadata();
                                JSONObject itemObj = (JSONObject) itemsArray.get(j);
                                if (itemObj.has("createdAt")) {
                                    review.setReviewTime(itemObj.getLong("createdAt"));
                                }

                                if (itemObj.has("text")) {
                                    review.setReviewContent(itemObj.getString("text"));
                                }

                                if (itemObj.has("user")) {
                                    JSONObject user = (JSONObject)itemObj.get("user");
                                    if (user.has("firstName")) {
                                        String name = user.getString("firstName");
                                        if (user.has("lastName")) {
                                            name += (" " + user.getString("lastName"));
                                        }
                                        review.setAuthorName(name);
                                    }

                                    if (user.has("photo")) {
                                        JSONObject profilePhoto = (JSONObject)user.get("photo");
                                        StringBuffer prefix = new StringBuffer();
                                        if (profilePhoto.has("prefix")) {
                                            prefix.append(profilePhoto.getString("prefix"));
                                            prefix.append("original");
                                            if (profilePhoto.has("suffix")) {
                                                prefix.append(profilePhoto.getString("suffix"));
                                            }
                                        }
                                        if (!TextUtils.isEmpty(prefix.toString())) {
                                            review.setProfilePhotoUrl(prefix.toString());
                                        }
                                    }

                                    if (user.has("id")) {
                                        String userId = user.getString("id");
                                        if (!TextUtils.isEmpty(userId)) {
                                            review.setAuthorUrl("https://foursquare.com/user/" + userId);
                                        }
                                    }
                                }

                                if (itemObj.has("lang")) {
                                    review.setLanguage(itemObj.getString("lang"));
                                }
                                mReviewList.add(review);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
