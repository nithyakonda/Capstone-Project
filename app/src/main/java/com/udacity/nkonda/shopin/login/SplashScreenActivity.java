package com.udacity.nkonda.shopin.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.storelist.StoreListActivity;


public class SplashScreenActivity extends BaseActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent();
//        if (isOnline()) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser == null) {
//                intent.setClass(this, LoginActivity.class);
//            } else {
//                intent.setClass(this, StoreListActivity.class);
//            }
//        } else {
//            intent.setClass(this, LoginActivity.class);
//        }
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }
}
