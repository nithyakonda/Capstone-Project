package com.udacity.nkonda.shopin.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.udacity.nkonda.shopin.R;

public class UiUtils {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showAlert(Context context, String title, String msg) {
        showAlert(context, title, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static void showAlert(Context context, String title, String msg, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = buildAlert(context, title, msg, clickListener);
        builder.create().show();
    }

    public static void showDefaultError(Context context) {
        showAlert(context, context.getString(R.string.dialog_title_default_error), context.getString(R.string.dialog_msg_default_error));
    }

    public static void showDefaultError(Context context, DialogInterface.OnClickListener clickListener) {
        showAlert(context, context.getString(R.string.dialog_title_default_error), context.getString(R.string.dialog_msg_default_error), clickListener);
    }

    private static AlertDialog.Builder buildAlert(Context context, String title, String msg, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.action_ok, clickListener);
        return builder;
    }
}
