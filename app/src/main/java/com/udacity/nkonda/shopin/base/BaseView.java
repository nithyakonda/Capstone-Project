package com.udacity.nkonda.shopin.base;

import com.udacity.nkonda.shopin.login.CheckConnectivityCallback;

public interface BaseView {
    public void isOnline(CheckConnectivityCallback connectivityCallback);
    public void showError();
    public void showError(String message);
}
