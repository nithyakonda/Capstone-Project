package com.udacity.nkonda.shopin.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.storelist.StoreListActivity;


public class SplashScreenActivity extends BaseActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ivLogo = findViewById(R.id.iv_logo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Intent intent = new Intent();
        final boolean[] currentUserLoggedIn = {false};
        final FirebaseUser[] currentUser = {null};
        isOnline(new CheckConnectivityCallback() {
            @Override
            public void done(boolean isOnline) {
                if (isOnline) {
                    currentUser[0] = mAuth.getCurrentUser();
                    currentUserLoggedIn[0] = currentUser[0] != null;
                }
                if (currentUserLoggedIn[0]) {
                    ShopinDatabase.getInstance();
                    intent.setClass(SplashScreenActivity.this, StoreListActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(SplashScreenActivity.this, LoginActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(SplashScreenActivity.this,
                                    ivLogo,
                                    ivLogo.getTransitionName());
                    startActivity(intent, options.toBundle());
                }
            }
        });
    }
}
