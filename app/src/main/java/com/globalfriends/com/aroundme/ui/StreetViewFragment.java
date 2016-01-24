package com.globalfriends.com.aroundme.ui;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalfriends.com.aroundme.R;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

/**
 * Created by Vishal on 1/24/2016.
 */
public class StreetViewFragment extends SupportStreetViewPanoramaFragment implements
        StreetViewPanorama.OnStreetViewPanoramaChangeListener {

    Double mLatitude;
    Double mLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View fragmentView = super.onCreateView(inflater, container, savedInstance);

        StreetViewPanorama panorama = getStreetViewPanorama();

        //Check we got a valid instance of the StreetViewPanorama
        if (panorama != null && getArguments() != null) {
            Bundle bundle = getArguments();
            mLatitude = bundle.getDouble("LATITUDE", -1);
            mLongitude = bundle.getDouble("LONGITUDE", -1);
            panorama.setPosition(new LatLng(mLatitude, mLongitude));
            panorama.setOnStreetViewPanoramaChangeListener(this);
        }
        return fragmentView;
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
        //Get the angle between the target location and road side location
        float bearing = 0;

        if (!((streetViewPanoramaLocation == null || (streetViewPanoramaLocation.position == null)
                || streetViewPanoramaLocation == null || (streetViewPanoramaLocation.position == null)))) {
            bearing = getBearing(streetViewPanoramaLocation.position.latitude,
                    streetViewPanoramaLocation.position.longitude,
                    mLatitude,
                    mLongitude);
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.error_dialog_title))
                    .setMessage(getResources().getString(R.string.missing_street_view_error))
                    .setCancelable(false)
                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().onBackPressed();
                        }
                    }).show();
            return;
        }

        //Remove the listener
        getStreetViewPanorama().setOnStreetViewPanoramaChangeListener(null);
        //Change the camera angle
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                .bearing(bearing)
                .build();
        getStreetViewPanorama().animateTo(camera, 1);
    }

    private float getBearing(double startLat, double startLng, double endLat, double endLng) {
        Location startLocation = new Location("startlocation");
        startLocation.setLatitude(startLat);
        startLocation.setLongitude(startLng);

        Location endLocation = new Location("endlocation");
        endLocation.setLatitude(endLat);
        endLocation.setLongitude(endLng);

        return startLocation.bearingTo(endLocation);
    }
}
