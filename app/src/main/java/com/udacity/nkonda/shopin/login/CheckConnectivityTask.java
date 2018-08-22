package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckConnectivityTask extends AsyncTask<Void, Void, Boolean> {
    private static final int HTTP_OK = 200;
    private static final String TEST_URL = "http://www.google.com";
    private CheckConnectivityCallback mCallback;

    public CheckConnectivityTask(CheckConnectivityCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(Boolean isOnline) {
        mCallback.done(isOnline);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL testUrl = new URL(TEST_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) testUrl
                    .openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "Test");
            httpURLConnection.setRequestProperty("Connection", "close");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();
            return httpURLConnection.getResponseCode() == HTTP_OK;
        }
        catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
