package com.globalfriends.com.aroundme.controller;

/**
 * Created by vishal on 11/8/2015.
 */
public class Controller {
    private static Controller sInstance;

    private Controller() {

    }

    public static Controller getInstance() {
        if (sInstance == null) {
            sInstance = new Controller();
        }
        return sInstance;
    }

    public void findPlaces(final double latitude, final double longitude) {
    }

    public void findPlaces(final String name) {
    }

    public void findPlaceDetails(final String placeId) {

    }
}
