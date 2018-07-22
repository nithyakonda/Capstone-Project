package com.udacity.nkonda.shopin.data.database;

import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

public interface ShopinDatabaseContract {
    interface AddUserCallback {
        void onResult(boolean success, Exception exception);
    }

    interface AddStoreCallback {
        void onResult(boolean success, Exception exception);
    }

    void addUser(User user, AddUserCallback callback);
    void addStore(String uid, Store store, AddStoreCallback callback);
}
