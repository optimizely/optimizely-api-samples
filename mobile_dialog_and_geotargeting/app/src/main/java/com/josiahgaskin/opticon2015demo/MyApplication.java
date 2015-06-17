package com.josiahgaskin.opticon2015demo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import com.optimizely.Optimizely;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * App Subclass
 */
public class MyApplication extends Application implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000; // 12 hours
    private static final int GEOFENCE_RADIUS_IN_METERS = 100;
    private static final String TAG = "OPT DEBUG";

    private static final Map<String, LatLng> SF_LANDMARKS = new HashMap<String, LatLng>();
    static {
        SF_LANDMARKS.put("Pier27", new LatLng(37.805497, -122.403386));
        SF_LANDMARKS.put("OptimizelyOffice", new LatLng(37.786208, -122.398714));
    }

    private List<Geofence> mGeofenceList = new ArrayList<Geofence>();

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        // Add current data to optimizely
        SharedPreferences prefs = getSharedPreferences("brickandmortar", MODE_PRIVATE);
        int totalVisits = prefs.getInt("TOTAL_VISITS", 0);
        Optimizely.setCustomTag("brickandmortar_TotalVisits", Integer.toString(totalVisits));
        for (String locationName : prefs.getStringSet("VISITED_SET", Collections.<String>emptySet())) {
            Optimizely.setCustomTag("brickandmortar_visited_" + locationName, "true");
        }

        String apiKey = getString(R.string.api_key);
        if (!apiKey.isEmpty()) {
            Optimizely.startOptimizely(apiKey, this);
        } else {
            Log.e("OPT DEMO", "No API key found! Please add your API token in the api_key.xml values file");
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        populateGeofenceList();
        sendGeofencingRequest();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: " + result.toString());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and sends a GeofencingRequest.
     */
    private  void sendGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        final GeofencingRequest request = builder.build();

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        final PendingIntent pendingIntent = PendingIntent
                .getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    request,
                    pendingIntent);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }


    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences or
     * fetch them from a server
     */
    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : SF_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())
                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            GEOFENCE_RADIUS_IN_METERS
                    )
                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    // Create the geofence.
                    .build());
        }
    }
}
