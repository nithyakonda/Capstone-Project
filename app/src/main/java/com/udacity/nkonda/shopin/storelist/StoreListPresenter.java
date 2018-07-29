package com.udacity.nkonda.shopin.storelist;

import android.util.Log;

import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;

import java.util.LinkedHashMap;
import java.util.List;

public class StoreListPresenter implements StoreListContract.Presenter {
    private static final String TAG = StoreListPresenter.class.getSimpleName();
    private static User sUser;

    private StoreListContract.View mView;
    private ShopinDatabase mDatabase;

    public StoreListPresenter(StoreListContract.View view) {
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
    public void addNewStoreAndCreateGeofence(final Store store) {
        if (sUser == null) {
            Log.e(TAG, "addNewStoreAndCreateGeofence:: user is null");
            // TODO: 7/21/18 handle null
            return;
        }
        mDatabase.addStore(sUser.getUid(), store, new ShopinDatabaseContract.OnCompletionCallback() {
            @Override
            public void onResult(boolean success, Exception exception) {
                if (success) {
                    mView.displayNewStore(store);
                    // TODO: 7/21/18 create geofence
                } else {
                    Log.e(TAG, "addNewStoreAndCreateGeofence:: add store to database failed:: " + exception);
                    // TODO: 7/21/18 handle error
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
        final LinkedHashMap<String, Boolean> dummyItems1 = new LinkedHashMap<>();
        dummyItems1.put("Eggs", true);
        dummyItems1.put("Milk", false);
        dummyItems1.put("Bread", false);

        if (sUser != null) {
            String initial = (sUser.getDisplayName() != null && !sUser.getDisplayName().isEmpty())
                    ? sUser.getDisplayName() : sUser.getEmail();
            mView.setupToolbar(initial.substring(0, 1).toUpperCase(), sUser.getDisplayPicture());
            mDatabase.getStores(sUser.getUid(), new ShopinDatabaseContract.GetStoresCallback() {
                @Override
                public void onResult(boolean success, Exception exception, List<Store> stores) {
                    if (success) {
                        for (Store store: stores
                        ) {
                            store.setItems(dummyItems1);
                        }
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
}
