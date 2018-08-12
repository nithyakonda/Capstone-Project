package com.udacity.nkonda.shopin.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.geofence.ShopinNotificationManager;
import com.udacity.nkonda.shopin.util.UiUtils;

public class BaseActivity extends AppCompatActivity implements BaseView{
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected FirebaseAuth mAuth;
    protected GeofencingClient mGeofencingClient;
    protected ShopinNotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mNotificationManager = ShopinNotificationManager.getInstance(this);
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

    @Override
    public void showError() {
        UiUtils.showDefaultError(this);
    }

    @Override
    public void showError(String message) {
        UiUtils.showAlert(this, "Error", message);
    }
}
