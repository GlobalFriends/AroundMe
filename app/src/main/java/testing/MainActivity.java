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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.globalfriends.com.aroundme.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;

import Logging.Logger;
import testing.yelp.SearchBarActivity;

/**
 * This class is used to search places using Places API using keywords like police,hospital etc.
 *
 * @author Karn Shah
 * @Date 10/3/2013
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = getClass().getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LatLng SAMSUNG_LOCATION = new LatLng(40.603277, -74.625254);
    private LatLngBounds mBounds;

    private GoogleMap mMap;
    private String[] places;
    private LocationManager locationManager;
    private Location loc;
    private LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            Logger.e(TAG, "location update : " + location);
            loc = location;
            locationManager.removeUpdates(listener);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();

        initCompo();
        places = getResources().getStringArray(R.array.places);
        places = getResources().getStringArray(R.array.places);
        currentLocation();
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
                            /*new GetPlaces(MainActivity.this,
                                    places[itemPosition].toLowerCase().replace(
                                            "-", "_").replace(" ", "_")).execute();*/
                            new GetPlacesTask(MainActivity.this).execute(places[itemPosition].toLowerCase());
                        }

                        mMap.clear();
                        new GetPlacesTask(MainActivity.this).execute(places[itemPosition].toLowerCase());
                        return true;
                    }

                });

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
                .addApi(LocationServices.API)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .build();
    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager
                .getBestProvider(new Criteria(), false);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            loc = location;
//            new GetPlaces(MainActivity.this, places[0].toLowerCase().replace(
//                    "-", "_")).execute();
            Logger.e(TAG, "location : " + location);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google API Client connection established");
        mBounds = new LatLngBounds(
                new LatLng(SAMSUNG_LOCATION.latitude - 2.0, SAMSUNG_LOCATION.longitude - 2.0),
                new LatLng(SAMSUNG_LOCATION.latitude + 2.0, SAMSUNG_LOCATION.longitude + 2.0));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google API Client connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Google API Client connection failed");
    }

    private class GetPlacesTask extends AsyncTask<String, Void, ArrayList<Place>> {

        private Context mContext;
        private ProgressDialog dialog;

        public GetPlacesTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(this.mContext);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(String... placeTypes) {
            Collection<Integer> filterTypes = new ArrayList<Integer>();
            if (placeTypes[0].equals("atm")) {
                filterTypes.add(Place.TYPE_ATM);
            } else if ("bank".equals(placeTypes[0])) {
                filterTypes.add(Place.TYPE_BANK);
            }

            if (mGoogleApiClient.isConnected()) {
                PendingResult<AutocompletePredictionBuffer> results =
                        com.google.android.gms.location.places.Places.GeoDataApi.getAutocompletePredictions(
                                mGoogleApiClient,
                                placeTypes[0],
                                mBounds,
                                null/*AutocompleteFilter.create(filterTypes)*/);
                AutocompletePredictionBuffer autocompletePredictions = results.await();
                final com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();

                if (!status.isSuccess()) {
                    autocompletePredictions.release();
                    return null;
                }

                ArrayList<AutocompletePrediction> autocompletePredictionArrayList =
                        DataBufferUtils.freezeAndClose(autocompletePredictions);

                String[] placeIds = new String[autocompletePredictionArrayList.size()];
                for (int i = 0; i < autocompletePredictionArrayList.size(); i++) {
                    AutocompletePrediction autocompletePrediction = autocompletePredictionArrayList.get(i);
                    placeIds[i] = autocompletePrediction.getPlaceId();
                }

                PendingResult<PlaceBuffer> result1 = com.google.android.gms.location.places.Places.GeoDataApi.getPlaceById(
                        mGoogleApiClient,
                        placeIds);
                PlaceBuffer places = result1.await();
                com.google.android.gms.common.api.Status status1 = places.getStatus();
                if (!status1.isSuccess()) {
                    places.release();
                    return null;
                }

                return DataBufferUtils.freezeAndClose(places);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result == null) {
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName().toString())
                        .position(new LatLng(result.get(i).getLatLng().latitude,
                                result.get(i).getLatLng().longitude))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin)));
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatLng().latitude, result
                            .get(0).getLatLng().longitude)) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

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
            for (int i = 0; i < result.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName())
                        .position(
                                new LatLng(result.get(i).getLatitude(), result
                                        .get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin))
                        .snippet(result.get(i).getVicinity()));
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

            for (int i = 0; i < findPlaces.size(); i++) {

                Places placeDetail = findPlaces.get(i);
                Logger.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;
        }

    }

}
