package com.udacity.nkonda.shopin.storelist;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;
import com.udacity.nkonda.shopin.geofence.ShopinGeofenceReceiver;
import com.udacity.nkonda.shopin.util.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class StoreListPresenter implements StoreListContract.Presenter {
    private static final String TAG = StoreListPresenter.class.getSimpleName();
    private static final int GEOFENCE_RADIUS_IN_METERS = 100;
    private static final int LOITERING_DELAY_IN_MS = 1000;
    private static User sUser;
    private static List<Store> sStores;

    private Context mContext;
    private StoreListContract.View mView;
    private ShopinDatabase mDatabase;
    private FirebaseAuth mAuth;
    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;

    public StoreListPresenter(Context context,
            StoreListContract.View view,
            FirebaseAuth auth, GeofencingClient geofencingClient) {
        mContext = context;
        mView = view;
        mAuth = auth;
        mGeofencingClient = geofencingClient;
        mDatabase = ShopinDatabase.getInstance();

        sUser = FirebaseUtils.getUser(mAuth.getCurrentUser());
    }

    @Override
    public void start(StoreListState state) {
        load();
    }

    @Override
    public StoreListState getState() {
        // no op
        return null;
    }

    @Override
    public void addStore(final Store store) {
        if (isStoreAlreadyAdded(store)) {
            mView.addItems(store);
            return;
        }
        mDatabase.addStore(sUser.getUid(), store, new ShopinDatabaseContract.OnCompletionCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onResult(boolean success, Exception exception) {
                if (success) {
                    addGeofence(store);
                } else {
                    Log.e(TAG, "addStore:: add store to database failed:: " + exception);
                    mView.showError(mContext.getString(R.string.dialog_message_add_store_failed));
                }
            }
        });
    }

    @Override
    public void deleteStore(final String storeId) {
        mDatabase.deleteStore(sUser.getUid(), storeId, new ShopinDatabaseContract.OnCompletionCallback() {
            @Override
            public void onResult(boolean success, Exception exception) {
                if (success) {
                    deleteGeofence(storeId);
                }
                Log.i(TAG, "deleteStore " + storeId + (success ? " success" : " failed"));
            }
        });
    }

    @Override
    public void load() {
        if (sUser != null) {
            String initial = (sUser.getDisplayName() != null && !sUser.getDisplayName().isEmpty())
                    ? sUser.getDisplayName() : sUser.getEmail();
            mView.setupToolbar(initial.substring(0, 1).toUpperCase(), sUser.getDisplayPicture());
            mDatabase.getStores(sUser.getUid(), new ShopinDatabaseContract.GetStoresCallback() {
                @Override
                public void onResult(boolean success, Exception exception, List<Store> stores) {
                    if (success) {
                        sStores = stores;
                        mView.displayStores(stores);
                    } else {
                        mView.showError();
                    }
                }
            });
        } else {
            mView.showError();
        }
    }

    @Override
    public void editItem(String storeId, final Item item) {
        mDatabase.editItem(storeId, item, new ShopinDatabaseContract.OnCompletionCallback() {
            @Override
            public void onResult(boolean success, Exception exception) {
                if (success) {

                } else {
                    Log.w(TAG, "editItem:: failed " + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }

    private boolean isStoreAlreadyAdded(Store store) {
        if (sStores != null) {
            for (Store store1 : sStores) {
                if (store1.getId().equals(store.getId())) {
                    Log.i(TAG, "addStore:: store already exists");
                    return true;
                }
            }
        }
        return false;
    }

    private void addGeofence(final Store store) {
        try {
            mGeofencingClient.addGeofences(getGeofencingRequest(store), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "addGeofence:: add geofence success, for store id " + store.getId());
                            mView.addItems(store);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "addGeofence:: add geofence failed, for store id " + store.getId());
                            deleteStore(store.getId());
                            mView.showError(mContext.getString(R.string.dialog_message_add_store_failed));
                            e.printStackTrace();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void deleteGeofence(final String storeId) {
        List<String> requestId = new ArrayList<>();
        requestId.add(storeId);
        mGeofencingClient.removeGeofences(requestId)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "deleteGeofence:: delete geofence success, for store id " + storeId);
                    } else {
                        Log.e(TAG, "deleteGeofence:: delete geofence failed, for store id " + storeId);
                    }
                }
            });
    }

    private GeofencingRequest getGeofencingRequest(Store store) {
        ArrayList<Geofence> geofences = new ArrayList<>();
        Geofence geofence = new Geofence.Builder()
                .setRequestId(store.getId())
                .setCircularRegion(
                        store.getCoordinates().getLatitude(),
                        store.getCoordinates().getLongitude(),
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(LOITERING_DELAY_IN_MS)
                .build();
        geofences.add(geofence);
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER |
                        GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(geofences)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent == null) {
            Intent intent = new Intent(mContext, ShopinGeofenceReceiver.class);
            mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);
        }
        return mGeofencePendingIntent;
    }
}
