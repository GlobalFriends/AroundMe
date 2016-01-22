package com.globalfriends.com.aroundme.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.globalfriends.com.aroundme.R;
import com.globalfriends.com.aroundme.data.IPlaceDetails;
import com.globalfriends.com.aroundme.data.PreferenceManager;
import com.globalfriends.com.aroundme.data.places.AutoCompletePredictionProvider;
import com.globalfriends.com.aroundme.data.places.PlaceInfo;
import com.globalfriends.com.aroundme.logging.Logger;
import com.globalfriends.com.aroundme.protocol.TransactionManager;
import com.globalfriends.com.aroundme.ui.placeList.FavoriteFragment;
import com.globalfriends.com.aroundme.ui.placeList.PlacesListFragment;
import com.globalfriends.com.aroundme.ui.placeList.RecentFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by vishal on 11/8/2015.
 */
public class Launcher extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SelectionFragment.OnSelectionFragmentSelection,
        PlacesListFragment.OnPlaceListFragmentSelection,
        PlaceDetailsFragment.OnPlaceDetailsFragmentInteractionListener,
        FavoriteFragment.OnFavoriteFragmentInteractionListener,
        RecentFragment.OnRecentFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ToolbarUpdateListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int SEARCH_TYPE_PLACE = 1;
    private static final int SEARCH_TYPE_LOCATION = 2;
    private static final int SEARCH_TYPE_DEFAULT = SEARCH_TYPE_PLACE;
    //Permission
    private final static int PLACE_LOCATOR_PERMISSIONS_ALL = 1;
    private final String TAG = getClass().getSimpleName();
    private Location loc;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private NavigationView mNavigationView;
    private boolean mIsCustomLocation;
    private MenuItem mSearchMenu;
    private MenuItem mSetLocationMenu;
    private SearchView mSearchView;
    private View mCustomLocationHolderView;
    private TextView mCustomLocationTextView;
    private Button mCustomLocationClearButton;
    private String mSavedCurrentLocationLatitude;
    private String mSavedCurrentLocationLongitude;
    private int mSearchType = SEARCH_TYPE_DEFAULT;
    /**
     * Search type result for location based search
     */
    private TransactionManager.Result mSetCustomLocationCallback = new TransactionManager.Result() {
        @Override
        public void onError(String errorMsg, String placeTag) {
            // Process only google responses
            if (TextUtils.isEmpty(placeTag) ||
                    !getString(R.string.google_places_tag).equalsIgnoreCase(placeTag)) {
                return;
            }

            enableCustomLocation(false, null);
        }

        @Override
        public void onGetPlaceDetails(IPlaceDetails response, String placeTag) {
            // Process only google responses
            if (TextUtils.isEmpty(placeTag) ||
                    !getString(R.string.google_places_tag).equalsIgnoreCase(placeTag)) {
                return;
            }

            enableCustomLocation(true, response);
        }
    };

    /**
     * Location update listener
     */
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.e(TAG, "onLocationChanged: " + location);
            loc = location;
            mSavedCurrentLocationLatitude = Double.toString(loc.getLatitude());
            mSavedCurrentLocationLongitude = Double.toString(loc.getLongitude());

            // If Custom location is set then do not update preference..
            if (!mIsCustomLocation) {
                PreferenceManager.putLocation(Double.toString(loc.getLatitude()),
                        Double.toString(loc.getLongitude()));
            }
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
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        TransactionManager.getInstance().resetResultCallback();
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
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
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
            locationManager.requestLocationUpdates(provider, 5000, 50.0f, listener);
        } else {
            loc = location;
            mSavedCurrentLocationLatitude = Double.toString(loc.getLatitude());
            mSavedCurrentLocationLongitude = Double.toString(loc.getLongitude());

            if (!mIsCustomLocation) {
                PreferenceManager.putLocation(Double.toString(loc.getLatitude()),
                        Double.toString(loc.getLongitude()));
            }
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

        if (savedInstanceState != null) {
            Log.i(TAG, "OnCreate with savedInstance");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        //Custom view layout update
        mCustomLocationHolderView = findViewById(R.id.custom_location);
        mCustomLocationTextView = (TextView) findViewById(R.id.text_current_location);

        // Handle Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            getAllPermissionForApp();
        } else {
            validateLocation();
        }
    }

    public void clearCustomLocation(View view) {
        enableCustomLocation(false, null);
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
        //new Exception().printStackTrace();
        Log.i(TAG, ">>> updateFragment fragment=" + fragment.getTag() + " first=" + first + " isBackStack" + isBackStack);
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
                transaction.addToBackStack(fragment.getTag());
            } else {
                for(int entry = 0; entry < getSupportFragmentManager().getBackStackEntryCount(); entry++){
                    getSupportFragmentManager().popBackStack();
                }
            }
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher_menu, menu);

        mSearchMenu = menu.findItem(R.id.action_search);
        mSetLocationMenu = menu.findItem(R.id.action_set_location);
        mSearchView = (SearchView) mSearchMenu.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                updateFragment(new SettingsFragment(), false, true);
                break;
            case R.id.action_search:
                AutoCompletePredictionProvider.mEnabled = false;
                mSearchView.setQueryHint(getString(R.string.set_search_hint));
                mSearchType = SEARCH_TYPE_PLACE;
                break;
            case R.id.action_set_location:
                AutoCompletePredictionProvider.mEnabled = true;
                mSearchMenu.expandActionView();
                mSearchView.setQueryHint(getString(R.string.set_location_hint));
                mSearchType = SEARCH_TYPE_LOCATION;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_my_location:
                String provider = locationManager.getBestProvider(new Criteria(), false);
                Location location = locationManager.getLastKnownLocation(provider);
                Bundle bundle = new Bundle();
                bundle.putDouble("LATITUDE", location.getLatitude());
                bundle.putDouble("LONGITUDE", location.getLongitude());
                bundle.putString("NAME", getString(R.string.current_location));
                Fragment locationFragment = new MapsFragment();
                locationFragment.setArguments(bundle);
                updateFragment(locationFragment, false, true);
                break;
            case R.id.drawer_recent:
                updateFragment(new RecentFragment(), false, false);
                break;
            case R.id.drawer_favorite:
                PreferenceManager.dump();
                updateFragment(new FavoriteFragment(), false, false);
                break;
            case R.id.drawer_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.developer_id), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                startActivity(Intent.createChooser(emailIntent, null));
                break;
            case R.id.drawer_rate_us:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(i);
                break;
            case R.id.drawer_share:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                startActivity(intent);
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnSelectionFragmentSelection(Bundle bundle) {
        launchPlaceListFragment(bundle);
    }

    private void launchPlaceListFragment(Bundle bundle) {
        Fragment fragment = new PlacesListFragment();
        fragment.setArguments(bundle);
        updateFragment(fragment, false, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (mSearchType == SEARCH_TYPE_LOCATION) {
                String placeId = intent.getDataString();

                if (placeId == null) {
                    return;
                }

                TransactionManager.getInstance().addResultCallback(mSetCustomLocationCallback);
                TransactionManager.getInstance().findGooglePlaceDetails(placeId, null);
            } else if (mSearchType == SEARCH_TYPE_PLACE) {
                mSearchMenu.collapseActionView();
                Bundle bundle = new Bundle();
                bundle.putString("TEXT_EXTRA", intent.getStringExtra(SearchManager.QUERY).replace(" ", "+"));
                launchPlaceListFragment(bundle);
            }
        }
    }

    /**
     * Enables custom location based on response
     *
     * @param result
     * @param response
     */
    private void enableCustomLocation(boolean result, IPlaceDetails response) {
        if (result) {
            mSearchMenu.collapseActionView();
            mCustomLocationHolderView.setVisibility(View.VISIBLE);
            mCustomLocationTextView.setText(response.getAddress());
            mIsCustomLocation = true;
            // Set received place details location as a base location for all search queries.
            PreferenceManager.putLocation(response.getLatitude().toString(),
                    response.getLongitude().toString());
            return;
        }

        TransactionManager.getInstance().removeResultCallback(mSetCustomLocationCallback);

        mIsCustomLocation = false;
        mCustomLocationTextView.setText("");
        mCustomLocationHolderView.setVisibility(View.GONE);
        PreferenceManager.putLocation(mSavedCurrentLocationLatitude,
                mSavedCurrentLocationLongitude);
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
        bundle.putString("PLACE_ID", place.getPlaceId());
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

    @Override
    public void onRecentViewClicked(String placeId) {
        Bundle bundle = new Bundle();
        bundle.putString("PLACE_ID", placeId);
        Fragment fragment = new PlaceDetailsFragment();
        fragment.setArguments(bundle);
        updateFragment(fragment, false, true);
    }

    @Override
    public void onFavoriteViewClicked(final String placeId) {
        Bundle bundle = new Bundle();
        bundle.putString("PLACE_ID", placeId);
        Fragment fragment = new PlaceDetailsFragment();
        fragment.setArguments(bundle);
        updateFragment(fragment, false, true);
    }

    @Override
    public void onMapsViewClicked(Bundle bundle) {
        Fragment fragment = new MapsFragment();
        fragment.setArguments(bundle);
        updateFragment(fragment, false, true);
    }

    @Override
    public void onNavigationEnabled(final boolean visibility) {
        mNavigationView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSearchBarEnabled(boolean visibility) {
        if (mSearchMenu != null) {
            mSearchMenu.setVisible(visibility);
        }

        // Set result callback for custom location only when set location
        // field is specifically enabled.
        if (mSetLocationMenu != null) {
            mSetLocationMenu.setVisible(visibility);
            if (mIsCustomLocation) {
                if (visibility) {
                    TransactionManager.getInstance().addResultCallback(mSetCustomLocationCallback);
                } else {
                    TransactionManager.getInstance().removeResultCallback(mSetCustomLocationCallback);
                }
                mCustomLocationHolderView.setVisibility(visibility ? View.VISIBLE : View.GONE);
            }
        }

        if (mSearchView != null) {
            mSearchView.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @TargetApi(23)
    public void getAllPermissionForApp() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            "com.google.android.providers.gsf.permission.READ_GSERVICES"},
                    PLACE_LOCATOR_PERMISSIONS_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        boolean grantedAccess = true;
        if (requestCode == PLACE_LOCATOR_PERMISSIONS_ALL) {
            if (grantResults.length > 0) {

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        grantedAccess = false;
                    }
                    if (!grantedAccess) {
                        finish();
                    }
                }
                if (grantedAccess) {
                    validateLocation();
                }
                Logger.i(TAG, "Read Contacts permission granted");
            } else {
                Logger.i(TAG, "Read Contacts permission denied");
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
