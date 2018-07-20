package com.udacity.nkonda.shopin.storelist;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            String initial = (name != null && !name.isEmpty()) ? name : email;
            mView.setupToolbar(initial.substring(0, 1).toUpperCase(), photoUrl);
            mView.displayStores(0);
        } else {
            mView.showError();
        }
    }
}
