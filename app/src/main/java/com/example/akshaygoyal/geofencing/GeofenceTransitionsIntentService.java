package com.example.akshaygoyal.geofencing;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.akshaygoyal.geofencing.Constants.*;

/**
 * Created by akshaygoyal on 9/25/15.
 */
public class GeofenceTransitionsIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    public GeofenceTransitionsIntentService() {
        super(LOG_TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(LOG_TAG, "Location Services error: " + errorCode);
        } else {

            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                // Connect to the Google Api service in preparation for sending a DataItem.
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                // Get the geofence id triggered. Note that only one geofence can be triggered at a
                // time in this example, but in some cases you might want to consider the full list
                // of geofences triggered.
                String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                        .getRequestId();
                // Create a DataItem with this geofence's id. The wearable can use this to create
                // a notification.
                final PutDataMapRequest putDataMapRequest =
                        PutDataMapRequest.create(GEOFENCE_DATA_ITEM_PATH);
                putDataMapRequest.getDataMap().putString(KEY_GEOFENCE_ID, triggeredGeoFenceId);
                if (mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.putDataItem(
                            mGoogleApiClient, putDataMapRequest.asPutDataRequest()).await();
                } else {
                    Log.e(LOG_TAG, "Failed to send data item: " + putDataMapRequest
                            + " - Client disconnected from Google Play Services");
                }
                Toast.makeText(this, getString(R.string.entering_geofence),
                        Toast.LENGTH_SHORT).show();
                mGoogleApiClient.disconnect();
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                // Delete the data item when leaving a geofence region.
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                Wearable.DataApi.deleteDataItems(mGoogleApiClient, GEOFENCE_DATA_ITEM_URI).await();
                showToast(this, R.string.exiting_geofence);
                mGoogleApiClient.disconnect();
            } else if (Geofence.GEOFENCE_TRANSITION_DWELL == transitionType) {
                // Delete the data item when leaving a geofence region.
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                Wearable.DataApi.deleteDataItems(mGoogleApiClient, GEOFENCE_DATA_ITEM_URI).await();
                showToast(this, R.string.dwelling_geofence);
                mGoogleApiClient.disconnect();
            }
        }
    }

    private void showToast(final Context context, final int resourceId) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    private String getGeofenceTransiionDetails(Context context,int transition,List<Geofence> triggeringGeofences){
        String geofenceTransitionString=getTransitionString(transition);
        ArrayList GeofenceIdsList=new ArrayList();
        for(Geofence geofence:triggeringGeofences){
            GeofenceIdsList.add(geofence.getRequestId());
        }
        String geofencesIdsString= TextUtils.join(", ", GeofenceIdsList);
        return geofenceTransitionString+": "+geofencesIdsString;
    }
    private String getTransitionString(int transitionType){
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered the geofence";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "Dwelling in the geofence";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited the geofence";
            default:return "Unknown Transition";
        }
    }
    /*private void sendNotification(String notificationDetails){
        Intent notificationIntent=new Intent(getApplicationContext(),MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationDetails)
                .setContentText("Click to return to app")
                .setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }*/

}
