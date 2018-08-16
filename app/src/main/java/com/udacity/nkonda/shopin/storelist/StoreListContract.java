package com.udacity.nkonda.shopin.storelist;

import android.net.Uri;

import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseState;
import com.udacity.nkonda.shopin.base.BaseView;
import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;

import java.util.List;

public interface StoreListContract {
    interface View extends BaseView {
        void setupToolbar(String initials, Uri photoUrl);
        void displayStores(List<Store> stores);
        void addItems(Store store);
    }

    interface Presenter extends BasePresenter<StoreListState> {
        void load();
        void addStore(Store store);
        void deleteStore(String storeId);
        void editItem(String storeId, Item item);
    }

    interface State extends BaseState {

    }
}
