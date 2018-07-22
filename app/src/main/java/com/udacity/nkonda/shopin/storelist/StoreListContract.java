package com.udacity.nkonda.shopin.storelist;

import android.net.Uri;

import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseState;
import com.udacity.nkonda.shopin.base.BaseView;
import com.udacity.nkonda.shopin.data.Store;

public interface StoreListContract {
    interface View extends BaseView {
        void setupToolbar(String initials, Uri photoUrl);
        void displayStores(int num);
        void displayNewStore(Store store);
    }

    interface Presenter extends BasePresenter<StoreListState> {
        void load();
        void addNewStoreAndCreateGeofence(Store store);
    }

    interface State extends BaseState {

    }
}
