package com.example.akshaygoyal.geofencing;

import android.content.Context;
import android.content.SharedPreferences;
import static com.example.akshaygoyal.geofencing.Constants.*;

/**
 * Created by akshaygoyal on 9/25/15.
 */
public class SimpleGeofenceStore {

    private final SharedPreferences mPrefs;
    private static final String SHARED_PREFERENCES = "SharedPreferences";

    public SimpleGeofenceStore(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public SimpleGeofence getGeofence(String id) {
        // Get the latitude for the geofence identified by id, or INVALID_FLOAT_VALUE if it doesn't
        // exist (similarly for the other values that follow).
        double lat = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_LATITUDE),
                INVALID_FLOAT_VALUE);
        double lng = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_LONGITUDE),
                INVALID_FLOAT_VALUE);
        float radius = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_RADIUS),
                INVALID_FLOAT_VALUE);
        long expirationDuration =
                mPrefs.getLong(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
                        INVALID_LONG_VALUE);
        int transitionType = mPrefs.getInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
                INVALID_INT_VALUE);
        // If none of the values is incorrect, return the object.
        if (lat != INVALID_FLOAT_VALUE
                && lng != INVALID_FLOAT_VALUE
                && radius != INVALID_FLOAT_VALUE
                && expirationDuration != INVALID_LONG_VALUE
                && transitionType != INVALID_INT_VALUE) {
            return new SimpleGeofence(id, lat, lng, radius, expirationDuration, transitionType);
        }
        // Otherwise, return null.
        return null;
    }

    public void setGeofence(String id, SimpleGeofence geofence) {
        // Get a SharedPreferences editor instance. Among other things, SharedPreferences
        // ensures that updates are atomic and non-concurrent.
        SharedPreferences.Editor prefs = mPrefs.edit();
        // Write the Geofence values to SharedPreferences.
        prefs.putFloat(getGeofenceFieldKey(id, KEY_LATITUDE), (float) geofence.getLatitude());
        prefs.putFloat(getGeofenceFieldKey(id, KEY_LONGITUDE), (float) geofence.getLongitude());
        prefs.putFloat(getGeofenceFieldKey(id, KEY_RADIUS), geofence.getRadius());
        prefs.putLong(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION), geofence.getExpirationDuration());
        prefs.putInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE), geofence.getTransitionType());
        // Commit the changes.
        prefs.commit();
    }

    public void clearGeofence(String id) {
        SharedPreferences.Editor prefs = mPrefs.edit();
        prefs.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
        prefs.remove(getGeofenceFieldKey(id, KEY_LONGITUDE));
        prefs.remove(getGeofenceFieldKey(id, KEY_RADIUS));
        prefs.remove(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION));
        prefs.remove(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE));
        prefs.commit();
    }

    private String getGeofenceFieldKey(String id, String fieldName) {
        return KEY_PREFIX + "_" + id + "_" + fieldName;
    }
}
