package com.globalfriends.com.aroundme.ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.ui.placeList.FavoriteFragment;
import com.globalfriends.com.aroundme.ui.placeList.PlacesListFragment;
import com.globalfriends.com.aroundme.ui.placeList.RecentFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import testing.MainActivity;

/**
 * Created by vishal on 11/8/2015.
 */
public class Launcher extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SelectionFragment.OnSelectionFragmentSelection,
        PlacesListFragment.OnPlaceListFragmentSelection,
        PlaceDetailsFragment.OnPlaceDetailsFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private static Context mContext;
    private final String TAG = getClass().getSimpleName();
    private Location loc;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.e(TAG, "onLocationChanged: " + location);
            loc = location;
            PreferenceManager.putLocation(Double.toString(loc.getLatitude()),
                    Double.toString(loc.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Logger.i(TAG, "onProviderEnabled extra=" + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Logger.i(TAG, "onProviderDisabled extra=" + s);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    private void validateLocation() {
        // Check for Location
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            LOCATION_REQUEST_CODE);
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        } else {
            registerLocationUpdates();
        }
    }

    private void registerLocationUpdates() {
        initGoogleClient();
        updateFragment(new SelectionFragment(), true, false);

        // Register for location updates
        String provider = locationManager.getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            locationManager.requestLocationUpdates(provider, 1000, 50.0f, listener);
        } else {
            loc = location;
            PreferenceManager.putLocation(Double.toString(loc.getLatitude()),
                    Double.toString(loc.getLongitude()));
        }
    }

    private void initGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.app_name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    registerLocationUpdates();
                } else {
                    finish();
                }
                break;
            default:
                finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_launcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Handle Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        validateLocation();
    }

    /**
     * Create or update fragement aand provide a back stack as per request
     *
     * @param fragment
     * @param first
     * @param isBackStack
     */
    private void updateFragment(final Fragment fragment, final boolean first,
                                boolean isBackStack) {
        if (findViewById(R.id.fragment_container) != null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            if (first) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commitAllowingStateLoss();
                return;
            }

            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            if (isBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_my_location:
                updateFragment(new MyLocationFragment(), false, true);
                break;
            case R.id.drawer_recent:
                updateFragment(new RecentFragment(), false, true);
                break;
            case R.id.drawer_favorite:
                PreferenceManager.dump();
                updateFragment(new FavoriteFragment(), false, true);
                break;
            case R.id.drawer_maps:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.drawer_feedback:
                Toast.makeText(this, "feedback", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_send:
                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_share:
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnSelectionFragmentSelection(String stringExtra) {
        Bundle bundle = new Bundle();
        bundle.putString("PLACE_EXTRA", stringExtra);
        Fragment fragment = new PlacesListFragment();
        fragment.setArguments(bundle);
        updateFragment(fragment, false, true);

//        Fragment fragment = new PlaceDetailsFragment();
//        updateFragment(fragment, false, true);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void OnPlaceListFragmentSelection(final PlaceInfo place) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("PLACE", place);
        Fragment fragment = new PlaceDetailsFragment();
        fragment.setArguments(bundle);
        updateFragment(fragment, false, true);
    }

    @Override
    public void handleFragmentSuicidal(String tag) {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
