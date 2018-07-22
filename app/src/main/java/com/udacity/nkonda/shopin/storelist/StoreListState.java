package com.udacity.nkonda.shopin.storelist;

import com.udacity.nkonda.shopin.data.User;

public class StoreListState implements StoreListContract.State {
    private User mUser;

    public StoreListState(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
