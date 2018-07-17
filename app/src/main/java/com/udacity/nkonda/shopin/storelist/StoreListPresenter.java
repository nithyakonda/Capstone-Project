package com.udacity.nkonda.shopin.storelist;

import com.udacity.nkonda.shopin.base.BaseState;

public class StoreListPresenter implements StoreListContract.Presenter{
    private StoreListContract.View mView;

    public StoreListPresenter(StoreListContract.View view) {
        mView = view;
    }

    @Override
    public void start(BaseState state) {

    }

    @Override
    public BaseState getState() {
        return null;
    }

    @Override
    public void addNewStore() {
        mView.displayStores(1);
    }

    @Override
    public void load() {
        mView.setupToolbar("K", "KondaNuthi's Shopping List");
        mView.displayStores(0);
    }
}
