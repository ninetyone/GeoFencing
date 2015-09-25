package com.example.akshaygoyal.geofencing;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.example.akshaygoyal.geofencing.Constants.SELLER_LOCATION;
import static com.example.akshaygoyal.geofencing.Constants.SELLER_LOCATION_LATITUDE;
import static com.example.akshaygoyal.geofencing.Constants.SELLER_LOCATION_LONGITUDE;
import static com.example.akshaygoyal.geofencing.Constants.SELLER_LOCATION_RADIUS_METERS;
import static com.example.akshaygoyal.geofencing.Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static com.example.akshaygoyal.geofencing.Constants.GEOFENCE_EXPIRATION_TIME;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    protected GoogleApiClient mGoogleApiClient;
    private List mGeofenceList;

    //Seller's Location
    private SimpleGeofence mAndroidBuildingGeofence;
    // Persistent storage for geofences.
    private SimpleGeofenceStore mGeofenceStorage;
    private LocationServices mLocationService;
    private PendingIntent mGeofenceRequestIntent;


    Button mStartTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isGooglePlayServicesAvailable()) {
            Log.e(LOG_TAG, "Google Play services unavailable.");
            finish();
            return;
        }

        buildGoogleApiClient();
        mGoogleApiClient.connect();
        mGeofenceStorage = new SimpleGeofenceStore(this);
        mGeofenceList = new ArrayList<Geofence>();
        createGeofences();


    }

    public void createGeofences() {

        mAndroidBuildingGeofence = new SimpleGeofence(
                SELLER_LOCATION,                // geofenceId.
                SELLER_LOCATION_LATITUDE,
                SELLER_LOCATION_LONGITUDE,
                SELLER_LOCATION_RADIUS_METERS,
                GEOFENCE_EXPIRATION_TIME,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL
        );
        // Store these flat versions in SharedPreferences and add them to the geofence list.
        mGeofenceStorage.setGeofence(SELLER_LOCATION, mAndroidBuildingGeofence);
        mGeofenceList.add(mAndroidBuildingGeofence.toGeofence());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "Google Play services is available.");
            }
            return true;
        } else {
            Log.e(LOG_TAG, "Google Play services is unavailable.");
            return false;
        }
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofenceList,
                mGeofenceRequestIntent);
        Toast.makeText(this, getString(R.string.start_geofence_service), Toast.LENGTH_SHORT).show();
        //finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If the error has a resolution, start a Google Play services activity to resolve it.
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(LOG_TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(LOG_TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, mGeofenceRequestIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
