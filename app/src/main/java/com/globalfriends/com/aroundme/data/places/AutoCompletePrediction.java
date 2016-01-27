package com.globalfriends.com.aroundme.data.places;

import android.text.TextUtils;

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

    public static List<AutoCompletePrediction> parse(JSONObject object, final boolean place) {

        try {
            JSONArray predictionsArray = object.getJSONArray("predictions");
            List<AutoCompletePrediction> predictions = new ArrayList<>();

            for (int i = 0; i < predictionsArray.length(); i++) {
                JSONObject predictionObject = (JSONObject) predictionsArray.get(i);
                AutoCompletePrediction prediction = new AutoCompletePrediction();

                if (predictionObject.has("description")) {
                    prediction.setDescription(predictionObject.getString("description"));
                }
                if (predictionObject.has("place_id")) {
                    prediction.setPlaceId(predictionObject.getString("place_id"));
                }

                if (!place) {
                    if (predictionObject.has("types")) {
                        boolean isGeoCode = false;
                        JSONArray placeTypes = predictionObject.getJSONArray("types");
                        if (placeTypes != null) {
                            for (int counter = 0; counter < placeTypes.length(); counter++) {
                                if ("geocode".equalsIgnoreCase(placeTypes.get(counter).toString())) {
                                    isGeoCode = true;
                                    break;
                                }
                            }
                        }

                        if (isGeoCode) {
                            continue;
                        }
                    }
                }

                if (!prediction.isEmpty()) {
                    predictions.add(prediction);
                }
            }

            return predictions;
        } catch (JSONException e) {
            Logger.getLogger(AutoCompletePrediction.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    private boolean isEmpty() {
        return (TextUtils.isEmpty(mDescription) && TextUtils.isEmpty(mPlaceId));
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }
}
