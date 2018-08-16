package com.udacity.nkonda.shopin.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;

import java.util.List;

public class ShopinGeofenceReceiver extends BroadcastReceiver {
    private static final String TAG = ShopinGeofenceReceiver.class.getSimpleName();

    private ShopinNotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        mNotificationManager = ShopinNotificationManager.getInstance(context);
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                Log.e(TAG, "onReceive::error::" + GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode()));
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


                // Send notification and log the transition details.
                sendNotification(triggeringGeofences);
            } else {
                // Log the error.
                Log.e(TAG, "onReceive::invalid transition type " + geofenceTransition);
            }
        }
    }

    private void sendNotification(List<Geofence> triggeringGeofences) {
        if (triggeringGeofences.size() == 1) {
            ShopinDatabase.getInstance().getStore(triggeringGeofences.get(0).getRequestId(), new ShopinDatabaseContract.GetStoreCallback() {
                @Override
                public void onResult(boolean success, Exception exception, Store store) {
                    if(success) {
                        if (store.getItems().size() > 0) {
                            Log.i(TAG, "sendNotification::sending notification for store " + store.getId());
                            mNotificationManager.notify(store);
                        }
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
