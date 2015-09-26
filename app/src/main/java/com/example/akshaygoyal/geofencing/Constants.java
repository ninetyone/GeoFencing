package com.example.akshaygoyal.geofencing;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by akshaygoyal on 9/25/15.
 */
public class Constants {

    public static final String PACKAGE_NAME = "com.example.akshaygoyal.geofencing";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 150;

    public static final HashMap<String, LatLng> SELLER_LANDMARKS = new HashMap<String, LatLng>();

    static {
        SELLER_LANDMARKS.put("Seller1", new LatLng(28.636279, 77.368558));
    }
}
