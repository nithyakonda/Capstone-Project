package com.udacity.nkonda.shopin.database;

import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;

import java.util.ArrayList;
import java.util.List;

public interface ShopinDatabaseContract {
    interface OnCompletionCallback {
        void onResult(boolean success, Exception exception);
    }

    interface GetStoresCallback {
        void onResult(boolean success, Exception exception, List<Store> stores);
    }

    interface GetStoreCallback {
        void onResult(boolean success, Exception exception, Store store);
    }

    interface GetItemsCallback {
        void onResult(boolean success, Exception exception, List<Item> items);
    }

    void addUser(User user, OnCompletionCallback callback);
    void addStore(String uid, Store store, OnCompletionCallback callback);
    void deleteStore(String uid, String storeId, OnCompletionCallback callback);
    void getStores(String uid, GetStoresCallback callback);
    void getStore(String storeID, GetStoreCallback callback);
    void getItems(String storeId, GetItemsCallback callback);
    void deleteItem(String storeId, String itemId, OnCompletionCallback callback);
    void editItem(String storeId, Item item, OnCompletionCallback callback);
}
