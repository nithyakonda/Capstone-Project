package com.udacity.nkonda.shopin.storelist;

import android.net.Uri;

import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseView;

public interface StoreListContract {
    interface View extends BaseView {
        void setupToolbar(String initials, Uri photoUrl);
        void displayStores(int num);
    }

    interface Presenter extends BasePresenter {
        void load();
        void addNewStore();
    }
}
