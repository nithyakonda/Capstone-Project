package com.udacity.nkonda.shopin.database;

import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

import java.util.List;

public interface ShopinDatabaseContract {
    interface AddUserCallback {
        void onResult(boolean success, Exception exception);
    }

    interface AddStoreCallback {
        void onResult(boolean success, Exception exception);
    }

    interface GetStoresCallback {
        void onResult(boolean success, Exception exception, List<Store> stores);
    }

    void addUser(User user, AddUserCallback callback);
    void addStore(String uid, Store store, AddStoreCallback callback);
    void getStores(String uid, GetStoresCallback callback);
}
