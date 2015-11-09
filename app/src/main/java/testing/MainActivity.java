package testing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.logging.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import testing.yelp.SearchBarActivity;

/**
 * This class is used to search places using Places API using keywords like police,hospital etc.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static double MILE = 1609.34;
    public static double mCurrentLatitude;
    public static double mCurrentLongitude;
    private final String TAG = getClass().getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private String[] places;
    private Location loc;
    private LocationManager locationManager;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.e(TAG, "location update: " + location);
            loc = location;
            mCurrentLatitude = loc.getLatitude();
            mCurrentLongitude = loc.getLongitude();
            locationManager.removeUpdates(listener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCompo();
        places = getResources().getStringArray(R.array.places);
        currentLocation();
        buildGoogleApiClient();
        /*mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

        final ActionBar actionBar = getActionBar();
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
                            mMap.clear();
                            new GetPlaces(MainActivity.this,
                                    places[itemPosition].toLowerCase().replace(
                                            "-", "_").replace(" ", "_")).execute();
                        }
                        return true;
                    }

                });

    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            locationManager.requestLocationUpdates(provider, 0, 0.0f, listener);
        } else {
            loc = location;
            mCurrentLatitude = loc.getLatitude();
            mCurrentLongitude = loc.getLongitude();
            new GetPlaces(MainActivity.this, places[0].toLowerCase().replace("-", "_")).execute();
            Logger.e(TAG, "location: " + location);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .build();
    }

    private void initCompo() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SearchBarActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Logger.i(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logger.i(TAG, "onConnectionFailed");
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

            for (int i = 0; i < result.size(); i++) {
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
                    .newCameraPosition(cameraPosition));
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
            ArrayList<Places> findPlaces = service.findPlaces(loc.getLatitude(), // 28.632808
                    loc.getLongitude(), places); //0 77.218276
            return findPlaces;
        }

    }

}
