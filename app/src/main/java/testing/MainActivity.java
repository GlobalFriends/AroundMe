package testing;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.Places;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.ui.PlacesListFragment;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import testing.yelp.SearchBarActivity;

/**
 * This class is used to search places using Places API using keywords like police,hospital etc.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 */
public class MainActivity extends AppCompatActivity {

    public static double MILE = 1609.34;
    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private String[] places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCompo();
        places = getResources().getStringArray(R.array.places);
        new GetPlaces(MainActivity.this, places[0].toLowerCase().replace("-", "_")).execute();
        /*mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

        /*final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
                        this, R.array.places, android.R.layout.simple_list_item_1),
                new ActionBar.OnNavigationListener() {

                    @Override
                    public boolean onNavigationItemSelected(int itemPosition,
                                                            long itemId) {
                        Logger.e(TAG,
                                places[itemPosition].toLowerCase().replace("-",
                                        "_"));
                        if (loc != null) {
                            //mMap.clear();
                            new GetPlaces(MainActivity.this,
                                    places[itemPosition].toLowerCase().replace(
                                            "-", "_").replace(" ", "_")).execute();
                        }
                        return true;
                    }

                });*/

    }

    private void initCompo() {
        /*mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_yelp:
                startActivity(new Intent(this, SearchBarActivity.class));
                break;
            default:
                //mMap.clear();
                new GetPlaces(MainActivity.this,
                        item.getTitle().toString().toLowerCase().replace(
                                "-", "_").replace(" ", "_")).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populatePlacesList(List<Places> places) {
        Parcelable[] parcelables = new Parcelable[places.size()];
        for (int i = 0; i < places.size(); i++) {
            parcelables[i] = places.get(i);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArray("PLACES_LIST", parcelables);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder, PlacesListFragment.instantiate(MainActivity.this, "com.globalfriends.com.aroundme.ui.PlacesListFragment", bundle));
        ft.commit();
    }

    private class getPlaceDetails extends AsyncTask<Void, Void, String> {
        private String mPlaceId;

        public getPlaceDetails(String placeId) {
            mPlaceId = placeId;
        }

        @Override
        protected String doInBackground(Void... params) {
            PlacesService service = new PlacesService(
                    getResources().getString(R.string.google_maps_key));
            return service.placeDetails(mPlaceId);
        }
    }

    /**
     * Find places based on Latitude and Longitude
     */
    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Places>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlaces(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPostExecute(ArrayList<Places> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result.size() == 0) {
                Logger.i(TAG, "No results found");
                return;
            }

            populatePlacesList(result);
            /*for (int i = 0; i < result.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName())
                        .position(
                                new LatLng(result.get(i).getLatitude(), result
                                        .get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin))
                        .snippet(result.get(i).getVicinity()));

                new getPlaceDetails(result.get(i).getPlaceId()).execute();
//                com.google.android.gms.location.places.Places.GeoDataApi.
//                        getPlaceById(mGoogleApiClient, result.get(i).getPlaceId())
//                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
//                            @Override
//                            public void onResult(PlaceBuffer places) {
//                                Logger.i(TAG, "is current a success " + places.getStatus().isSuccess());
//                                if (places.getStatus().isSuccess()) {
//                                    final Place myPlace = places.get(0);
//                                    Logger.i(TAG, "Place found with details: " + myPlace.toString());
//                                }
//                                places.release();
//                            }
//                        });
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                            .get(0).getLongitude())) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));*/
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Places> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    getResources().getString(R.string.google_maps_key));
            ArrayList<Places> findPlaces = service.findPlaces(PreferenceManager.getLocation(), places); //0 77.218276
            return findPlaces;
        }

    }

}
