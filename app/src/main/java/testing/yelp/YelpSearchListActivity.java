package testing.yelp;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Logging.Logger;

public class YelpSearchListActivity extends ListActivity {
    private static final String TAG = "YelpSearchListActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTitle("Finding Tacos...");

        Intent intent = getIntent();
        final String searchTerm = intent.getData().getQueryParameter("term");
        final String searchLocation = intent.getData().getQueryParameter("location");
        final String lattitude = intent.getData().getQueryParameter("lattitude");
        final String longitude = intent.getData().getQueryParameter("longitude");
        final boolean isLocationEnabled = intent.getBooleanExtra("current_location", false);

        Logger.i(TAG, "Yelp lattitude=" + lattitude + " Longitude=" + longitude);
        setProgressBarIndeterminateVisibility(true);
        new AsyncTask<Void, Void, List<Business>>() {
            @Override
            protected List<Business> doInBackground(Void... params) {
                String businesses = null;
                if (isLocationEnabled) {
                    businesses = Yelp.getYelp(YelpSearchListActivity.this).search(searchTerm, Double.valueOf(lattitude),
                            Double.valueOf(longitude), 2);
                } else {
                    businesses = Yelp.getYelp(YelpSearchListActivity.this).search(searchTerm, searchLocation, 10);
                }

                try {
                    return processJson(businesses);
                } catch (JSONException e) {
                    return Collections.emptyList();
                }
            }

            @Override
            protected void onPostExecute(List<Business> businesses) {
                setTitle("Tacos Found");
                setProgressBarIndeterminateVisibility(false);
                getListView().setAdapter(new ArrayAdapter<Business>(YelpSearchListActivity.this, android.R.layout.simple_list_item_1, businesses));
            }
        }.execute();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Business biz = (Business) listView.getItemAtPosition(position);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(biz.url)));
    }

    List<Business> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        ArrayList<Business> businessObjs = new ArrayList<Business>(businesses.length());
        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            businessObjs.add(new Business(business.optString("name"), business.optString("mobile_url")));
        }
        return businessObjs;
    }

    class Business {
        final String name;
        final String url;

        public Business(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
