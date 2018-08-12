package com.udacity.nkonda.shopin.geofence;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;

import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    private String mUid;

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                Log.e(TAG, "onHandleIntent::error::" + geofencingEvent.getErrorCode());
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


                // Send notification and log the transition details.
                sendNotification(triggeringGeofences);
            } else {
                // Log the error.
                Log.e(TAG, "onHandleIntent::invalid transition type " + geofenceTransition);
            }
        }
    }

    private void sendNotification(List<Geofence> triggeringGeofences) {
        if (triggeringGeofences.size() == 1) {
            ShopinDatabase.getInstance().getStore(triggeringGeofences.get(0).getRequestId(), new ShopinDatabaseContract.GetStoreCallback() {
                @Override
                public void onResult(boolean success, Exception exception, Store store) {
                    if(success) {
                        Log.i(TAG, "sendNotification::sending notification for store " + store.getId());
                        // TODO: 7/29/18 send notification
                    } else {
                        Log.e(TAG, "sendNotification::GetStoreCallback::onResult" + exception.getMessage());
                    }
                }
            });
        } else {
            Log.wtf(TAG, "sendNotification:: there are multiple triggering geo fences");
        }
    }
}
