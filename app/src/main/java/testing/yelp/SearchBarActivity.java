package testing.yelp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;

public class SearchBarActivity extends Activity {

    private EditText mSearchTerm;
    private EditText mSearchLocation;
    private EditText mPhoneNumber;
    private CheckBox mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yelp_search_layout);
        setTitle("Search teh yelpz");
        mSearchTerm = (EditText) findViewById(R.id.searchTerm);
        mSearchLocation = (EditText) findViewById(R.id.searchLocation);
        mCurrentLocation = (CheckBox) findViewById(R.id.current_location);
        mPhoneNumber = (EditText) findViewById(R.id.phone_number_edit_box);
    }

    public void search(View v) {
        String term = mSearchTerm.getText().toString();
        String location = mSearchLocation.getText().toString();
        Intent intent = new Intent(this, YelpSearchListActivity.class);
        intent.putExtra("current_location", mCurrentLocation.isChecked());

        intent.setData(new Uri.Builder().appendQueryParameter("term", term)
                .appendQueryParameter("location", location)
                .appendQueryParameter("phone_number", mPhoneNumber.getText() != null ? mPhoneNumber.getText().toString() : null)
                .appendQueryParameter("lattitude", PreferenceManager.getLatitude())
                .appendQueryParameter("longitude", PreferenceManager.getLongitude()).build());
        startActivity(intent);
    }


}
