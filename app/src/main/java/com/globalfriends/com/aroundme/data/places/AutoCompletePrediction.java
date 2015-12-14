package com.globalfriends.com.aroundme.data.places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoCompletePrediction {
    private String mDescription;
    private String mPlaceId;

    public static List<AutoCompletePrediction> parse(JSONObject object) {

        try {
            JSONArray predictionsArray = object.getJSONArray("predictions");
            List<AutoCompletePrediction> predictions = new ArrayList<>();

            for (int i = 0; i < predictionsArray.length(); i++) {
                JSONObject predictionObject = (JSONObject) predictionsArray.get(i);
                AutoCompletePrediction prediction = new AutoCompletePrediction();
                prediction.setDescription(predictionObject.getString("description"));
                prediction.setPlaceId(predictionObject.getString("place_id"));
                predictions.add(prediction);
            }

            return predictions;
        } catch (JSONException e) {
            Logger.getLogger(AutoCompletePrediction.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public String getPlaceId() {
        return mPlaceId;
    }
}
