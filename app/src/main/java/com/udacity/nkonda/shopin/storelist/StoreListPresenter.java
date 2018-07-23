package com.udacity.nkonda.shopin.storelist;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.data.database.ShopinDatabase;
import com.udacity.nkonda.shopin.data.database.ShopinDatabaseContract;
import com.udacity.nkonda.shopin.util.FirebaseUtil;

public class StoreListPresenter implements StoreListContract.Presenter {
    private static final String TAG = StoreListPresenter.class.getSimpleName();
    private static User sUser;

    private StoreListContract.View mView;

    public StoreListPresenter(StoreListContract.View view) {
        mView = view;
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
        ShopinDatabase.getInstance().addStore(sUser.getUid(), store, new ShopinDatabaseContract.AddStoreCallback() {
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
    public void load() {
        if (sUser != null) {
            String initial = (sUser.getDisplayName() != null && !sUser.getDisplayName().isEmpty())
                    ? sUser.getDisplayName() : sUser.getEmail();
            mView.setupToolbar(initial.substring(0, 1).toUpperCase(), sUser.getDisplayPicture());
            // TODO: 7/22/18 get stores from DB
//            mView.displayStores(0);
        } else {
            mView.showError();
        }
    }
}
