package com.udacity.nkonda.shopin.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

import java.util.ArrayList;
import java.util.List;

public class ShopinDatabase implements ShopinDatabaseContract {
    private static final String TAG = ShopinDatabase.class.getSimpleName();
    private static ShopinDatabase sInstance;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersRef = mDatabase.getReference("users");

    private ShopinDatabase(){};

    public static ShopinDatabase getInstance() {
        if (sInstance == null) {
            sInstance = new ShopinDatabase();
        }
        return sInstance;
    }

    @Override
    public void addUser(User user, final AddUserCallback callback) {
        mUsersRef.child(user.getUid()).child("email").setValue(user.getEmail())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    callback.onResult(task.isSuccessful(), task.getException());
                }
            });
    }

    @Override
    public void addStore(String uid, Store store, final AddStoreCallback callback) {
        DatabaseReference storesRef = mUsersRef.child(uid).child("stores");
        storesRef.child(store.getId()).setValue(store)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    callback.onResult(task.isSuccessful(), task.getException());
                }
            });
    }

    @Override
    public void getStores(String uid, final GetStoresCallback callback) {
        final List<Store> stores = new ArrayList<>();
        final DatabaseReference storesRef = mUsersRef.child(uid).child("stores");
        storesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot storeSnapShot: dataSnapshot.getChildren()) {
                    stores.add(storeSnapShot.getValue(Store.class));
                }
                callback.onResult(true, null, stores);
                storesRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                callback.onResult(false, databaseError.toException(), null);
                storesRef.removeEventListener(this);
            }
        });
    }
}
