package com.udacity.nkonda.shopin.itemlist;

import android.util.Log;

import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;

import java.util.ArrayList;
import java.util.List;

public class ItemListPresenter implements ItemListContract.Presenter {
    private static final String TAG = ItemListPresenter.class.getSimpleName();
    private static String sStoreId;
    private static List<Item> sItems;

    private ItemListContract.View mView;
    private ShopinDatabase mDatabase;

    public ItemListPresenter(ItemListContract.View view) {
        mView = view;
        mDatabase = ShopinDatabase.getInstance();
    }

    @Override
    public void start(ItemListState state) {
        if (state != null) {
            sStoreId = state.getStoreId();
            sItems = state.getItems();
        }
        load();
    }

    @Override
    public ItemListState getState() {
        return new ItemListState(sStoreId, sItems);
    }

    @Override
    public void load() {
        mDatabase.getStore(sStoreId, new ShopinDatabaseContract.GetStoreCallback() {
            @Override
            public void onResult(boolean success, Exception exception, Store store) {
                if (success) {
                    mView.setupToolbar(store.getName(), store.getAddress());
                } else {
                    exception.printStackTrace();
                    Log.e(TAG, "load::getStore failed " + exception.getMessage());
                }
            }
        });
        mDatabase.getItems(sStoreId, new ShopinDatabaseContract.GetItemsCallback() {
            @Override
            public void onResult(boolean success, Exception exception, List<Item> items) {
                if (success) {
                    sItems = items;
                } else {
                    sItems = new ArrayList<>();
                    exception.printStackTrace();
                    Log.e(TAG, "load::getItems failed " + exception.getMessage());
                    // TODO: 8/6/18 handle error
                }
                mView.displayItems(sItems);
            }
        });
    }

    @Override
    public void addItem(Item item) {
        sItems.add(item);
    }

    @Override
    public void deleteItem(final Item item) {
        if (!item.getName().isEmpty()) {
            mDatabase.deleteItem(sStoreId, item.getId(), new ShopinDatabaseContract.OnCompletionCallback() {
                @Override
                public void onResult(boolean success, Exception exception) {
                    if (success) {
                        sItems.remove(item);
                    } else {
                        Log.w(TAG, "deleteItem:: failed " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            });
        } else {
            sItems.remove(item);
        }
    }

    @Override
    public void editItem(final Item item) {
        mDatabase.editItem(sStoreId, item, new ShopinDatabaseContract.OnCompletionCallback() {
            @Override
            public void onResult(boolean success, Exception exception) {
                if (success) {
                    sItems.set(sItems.indexOf(item), item);
                } else {
                    Log.w(TAG, "editItem:: failed " + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });
    }
}
