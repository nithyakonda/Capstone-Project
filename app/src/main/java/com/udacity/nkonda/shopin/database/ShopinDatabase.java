package com.udacity.nkonda.shopin.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopinDatabase implements ShopinDatabaseContract {
    private static final String TAG = ShopinDatabase.class.getSimpleName();
    private static ShopinDatabase sInstance;
    private String mUid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private DatabaseReference mStoresRef;

    private ShopinDatabase(){
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mUsersRef = mDatabase.getReference("users");
        mStoresRef = mUsersRef.child(mUid).child("stores");
    };

    public static ShopinDatabase getInstance() {
        if (sInstance == null) {
            sInstance = new ShopinDatabase();
        }
        return sInstance;
    }

    public String getNewItemId(String storeId) {
        final DatabaseReference itemsRef = mStoresRef.child(storeId).child("items");
        return itemsRef.push().getKey();
    }

    @Override
    public void addUser(User user, final OnCompletionCallback callback) {
        mUsersRef.child(user.getUid()).child("email").setValue(user.getEmail())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    callback.onResult(task.isSuccessful(), task.getException());
                }
            });
    }

    @Override
    public void addStore(String uid, Store store, final OnCompletionCallback callback) {
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
    public void deleteStore(String uid, String storeId, final OnCompletionCallback callback) {
        // TODO: 7/28/18 set uid in constructor instead of passing it
        DatabaseReference storeRef = mUsersRef.child(uid).child("stores").child(storeId);
        storeRef.removeValue()
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
                Log.w(TAG, "getStores:onCancelled", databaseError.toException());
                callback.onResult(false, databaseError.toException(), null);
                storesRef.removeEventListener(this);
            }
        });
    }

    @Override
    public void getStore(String storeID, final GetStoreCallback callback) {
        final DatabaseReference storeRef = mUsersRef.child(mUid).child("stores").child(storeID);
        storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.getValue(Store.class);
                callback.onResult(true, null, store);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onResult(false, databaseError.toException(), null);
            }
        });
    }

    @Override
    public void getItems(String storeId, final GetItemsCallback callback) {
        final List<Item> items = new ArrayList<>();
        final DatabaseReference itemsRef = mStoresRef.child(storeId).child("items");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemShapShot : dataSnapshot.getChildren()) {
                    items.add(itemShapShot.getValue(Item.class));
                }
                callback.onResult(true, null, items);
                itemsRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getItems:onCancelled", databaseError.toException());
                callback.onResult(false, databaseError.toException(), null);
                itemsRef.removeEventListener(this);
            }
        });
    }

    @Override
    public void deleteItem(String storeId, String itemId, final OnCompletionCallback callback) {
        final DatabaseReference itemsRef = mStoresRef.child(storeId).child("items").child(itemId);
        itemsRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onResult(task.isSuccessful(), task.getException());
                    }
                });
    }

    @Override
    public void editItem(String storeId, Item item, final OnCompletionCallback callback) {
        final DatabaseReference itemsRef = mStoresRef.child(storeId).child("items").child(item.getId());
        itemsRef.setValue(item)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onResult(task.isSuccessful(), task.getException());
                    }
                });
    }
}
