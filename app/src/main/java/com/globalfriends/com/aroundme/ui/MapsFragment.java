package com.globalfriends.com.aroundme.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalfriends.com.aroundme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapsFragment extends BaseFragment {
    MapView mMapView;
    private double mLatitude;
    private double mLongitude;
    private String mName;
    private int mMapType = GoogleMap.MAP_TYPE_HYBRID;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_location_info, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLatitude = bundle.getDouble("LATITUDE");
            mLongitude = bundle.getDouble("LONGITUDE");
            mName = bundle.getString("NAME");
            if (bundle.getInt("MAP_TYPE") != 0) {
                mMapType = bundle.getInt("MAP_TYPE");
            }
        }

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(mLatitude, mLongitude)).title(TextUtils.isEmpty(mName) ?
                getString(R.string.current_location) : mName);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mLatitude, mLongitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        googleMap.setMapType(mMapType);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        getActivity().setTitle(mName);
        mToolbarUpdater.onSearchBarEnabled(false);
        mToolbarUpdater.onNavigationEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}