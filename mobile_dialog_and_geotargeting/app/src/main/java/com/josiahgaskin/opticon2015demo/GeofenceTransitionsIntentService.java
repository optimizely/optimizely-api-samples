package com.josiahgaskin.opticon2015demo;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to receive geofencing notifications
 */
public class GeofenceTransitionsIntentService extends IntentService {
    public GeofenceTransitionsIntentService() {
        super("Optimizely Geofencing");
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            Log.e("OPT DEMO", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            SharedPreferences prefs = getSharedPreferences("brickandmortar", Context.MODE_PRIVATE);
            Set<String> visitedSet = new HashSet<String>(prefs.getStringSet("VISITED_SET",
                    Collections.<String>emptySet()));
            int newTotal = prefs.getInt("TOTAL_VISITS", 0);
            for (Geofence fence : geofencingEvent.getTriggeringGeofences()) {
                newTotal++;
                visitedSet.add(fence.getRequestId());
                Log.i("OPT DEMO", "Visited " + fence.getRequestId());
            }
            prefs.edit()
                 .putStringSet("VISITED_SET", visitedSet)
                 .putInt("TOTAL_VISITS", newTotal)
                 .apply();
        }
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown Error";
        }
    }
}
