package com.udacity.nkonda.shopin.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
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
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent();
//                intent.setClass(SplashScreenActivity.this, LoginActivity.class);
//                ActivityOptionsCompat options = ActivityOptionsCompat.
//                        makeSceneTransitionAnimation(SplashScreenActivity.this,
//                                ivLogo,
//                                ivLogo.getTransitionName());
//                startActivity(intent, options.toBundle());
//            }
//        }, 2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                boolean currentUserLoggedIn = false;
                if (isOnline()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    currentUserLoggedIn = currentUser != null;
                }
                if (currentUserLoggedIn) {
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
        }, 500);


    }
}
