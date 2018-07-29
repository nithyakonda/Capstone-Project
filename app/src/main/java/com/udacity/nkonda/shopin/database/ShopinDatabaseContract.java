package com.udacity.nkonda.shopin.database;

import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

import java.util.List;

public interface ShopinDatabaseContract {
    interface OnCompletionCallback {
        void onResult(boolean success, Exception exception);
    }

    interface GetStoresCallback {
        void onResult(boolean success, Exception exception, List<Store> stores);
    }

    void addUser(User user, OnCompletionCallback callback);
    void addStore(String uid, Store store, OnCompletionCallback callback);
    void deleteStore(String uid, String storeId, OnCompletionCallback callback);
    void getStores(String uid, GetStoresCallback callback);
}
