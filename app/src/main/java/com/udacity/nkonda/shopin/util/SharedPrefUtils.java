package com.udacity.nkonda.shopin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefUtils {
    public final static String PREF_LOGIN_EMAIL = "PREF_LOGIN_EMAIL";

    public static boolean clear(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    public static boolean contains(Context ctx, String preferenceName) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).contains(preferenceName);
    }

    public static void remove(Context ctx, String preferenceName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(preferenceName);
        editor.commit();
    }

    public static void set(Context ctx, String preferenceName, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(preferenceName, value);
        editor.commit();
    }

    public static String get(Context ctx, String preferenceName, String fallback) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                preferenceName,
                fallback
        );
    }
}
