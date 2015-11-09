package testing;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create request for Places API.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 */
public class PlacesService {
    private static final String TAG = "PlacesService";
    private String API_KEY = "";// "AIzaSyBBOveUtIw5LsuYVs4FLw6In7mVQMm3QLQ";

    public PlacesService(String apikey) {
        this.API_KEY = apikey;
    }

    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }

    public ArrayList<Places> findPlaces(double latitude, double longitude,
                                        String placeSpacification) {

        String urlString = makeUrl(latitude, longitude, placeSpacification);

        try {
            String json = getJSON(urlString);

            Logging.Logger.i(TAG, json);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            ArrayList<Places> arrayList = new ArrayList<Places>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Places place = Places
                            .jsonToPontoReferencia((JSONObject) array.get(i));
//                    Logging.Logger.i("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                }
            }
            return arrayList;
        } catch (JSONException ex) {
            Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    public String placeDetails(String placeId) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/details/json?");
        if (!TextUtils.isEmpty(placeId)) {
            urlString.append("placeid=" + placeId);
            urlString.append("&key=" + API_KEY);
        }
        String json = getJSON(urlString.toString());
        Logging.Logger.i(TAG + "PlaceDetails >>>", json);
        return json;
    }
    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude, String place) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=5000");
            // urlString.append("&types="+place);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=5000");
            urlString.append("&types=" + place);
            urlString.append("&sensor=false&key=" + API_KEY);
        }
        return urlString.toString();
    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}