package com.globalfriends.com.aroundme.data.yelp;

import com.globalfriends.com.aroundme.data.DefaultPlaceDetails;
import com.globalfriends.com.aroundme.data.PlaceReviewMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vishal on 11/19/2015.
 * Yelp provides very few details such as One review, review comment, image url, profile url, weburl and rating image
 */
public class YelpPlaceDetailsJson extends DefaultPlaceDetails {

    public YelpPlaceDetailsJson(JSONObject response) {
        super(response);
        JsonTOPointToReference(mResponse);
    }


    private YelpPlaceDetailsJson JsonTOPointToReference(JSONObject response) {
        try {
            // For future references
            int total = response.getInt("total");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray businessArray = response.getJSONArray("businesses");
            if (businessArray == null || businessArray.length() == 0) {
                return null;
            }

            JSONObject busineess = (JSONObject) businessArray.get(0);
            // Start Business object parser
            // getRatingDetails
            if (busineess.has("rating")) {
                mPlaceRating = busineess.getString("rating");
            }

            if (busineess.has("mobile_url")) {
                mWebUrl = busineess.getString("mobile_url");
            }

            if (busineess.has("review_count")) {
                mReviewCount = busineess.getInt("review_count");
            }

            if (busineess.has("rating_img_url")) {
                mRatingUrl = busineess.getString("rating_img_url_large");
            }
            // Reviewer Details:
            PlaceReviewMetadata review = new PlaceReviewMetadata();
            if (busineess.has("snippet_text")) {
                review.setReviewContent(busineess.getString("snippet_text"));
            }

            if (busineess.has("snippet_image_url")) {
                review.setProfilePhotoUrl(busineess.getString("snippet_image_url"));
            }
            updateReviewToList(review);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
