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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;
import com.udacity.nkonda.shopin.geofence.GeofenceTransitionsIntentService;

import java.util.ArrayList;
import java.util.List;

public class StoreListPresenter implements StoreListContract.Presenter {
    private static final String TAG = StoreListPresenter.class.getSimpleName();
    private static final int GEOFENCE_RADIUS_IN_METERS = 100;
    private static final int LOITERING_DELAY_IN_MS = 300000;
    private static User sUser;

    private Context mContext;
    private StoreListContract.View mView;
    private ShopinDatabase mDatabase;
    private PendingIntent mGeofencePendingIntent;

    public StoreListPresenter(Context context, StoreListContract.View view) {
        mContext = context;
        mView = view;
        mDatabase = ShopinDatabase.getInstance();
    }

    @Override
    public void start(StoreListState state) {
        if (state != null) {
            sUser = state.getUser();
        }
        load();
    }

    @Override
    public StoreListState getState() {
        return new StoreListState(sUser);
    }

    @Override
    public void addNewStoreAndCreateGeofence(final GeofencingClient geofencingClient, final Store store) {
        if (sUser == null) {
            Log.e(TAG, "addNewStoreAndCreateGeofence:: user is null");
            // TODO: 7/21/18 handle null
            return;
        }
        mDatabase.addStore(sUser.getUid(), store, new ShopinDatabaseContract.OnCompletionCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onResult(boolean success, Exception exception) {
                if (success) {
                    geofencingClient.addGeofences(getGeofencingRequest(store), getGeofencePendingIntent())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "addNewStoreAndCreateGeofence:: add geofence success, for store id " + store.getId());
                                    mView.addItems(store); // TODO: 7/29/18 navigate to itemslist activity
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "addNewStoreAndCreateGeofence:: add geofence failed, for store id " + store.getId());
                                    deleteStore(store.getId());
                                    mView.showError(mContext.getString(R.string.dialog_message_add_store_failed));
                                    e.printStackTrace();
                                }
                            });
                } else {
                    Log.e(TAG, "addNewStoreAndCreateGeofence:: add store to database failed:: " + exception);
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
            Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
            mGeofencePendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);
        }
        return mGeofencePendingIntent;
    }
}
