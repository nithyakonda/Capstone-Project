package com.udacity.nkonda.shopin.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity implements BaseView{
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean online = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!online) {
            Log.e(TAG, "No network connectivity");
        }
        return online;
    }
}
