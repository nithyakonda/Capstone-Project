package com.udacity.nkonda.shopin.data.database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

public class ShopinDatabase implements ShopinDatabaseContract {
    private static final String TAG = ShopinDatabase.class.getSimpleName();
    private static ShopinDatabase sInstance;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersRef = mDatabase.getReference("users");
    private DatabaseReference mStoresRef = mDatabase.getReference("users/stores");

    private ShopinDatabase(){};

    public static ShopinDatabase getInstance() {
        if (sInstance == null) {
            sInstance = new ShopinDatabase();
        }
        return sInstance;
    }

    @Override
    public void addUser(User user, final AddUserCallback callback) {
        mUsersRef.child(user.getUid()).setValue(user.getEmail())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    callback.onResult(task.isSuccessful(), task.getException());
                }
            });
    }

    @Override
    public void addStore(Store store, AddStoreCallback callback) {

    }
}
