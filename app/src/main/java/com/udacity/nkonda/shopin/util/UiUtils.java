package com.udacity.nkonda.shopin.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.udacity.nkonda.shopin.R;

import java.io.IOException;
import java.io.InputStream;

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

    public static Bitmap getAvatarBitmap(Context context, Uri photoUri) {
        Bitmap bitmap = null;
        try {
            bitmap = rotateImageIfRequired(context,
                    MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri),
                    photoUri);
        } catch (IOException e) {
            e.printStackTrace();
            UiUtils.showDefaultError(context);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private static AlertDialog.Builder buildAlert(Context context, String title, String msg, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.action_ok, clickListener);
        return builder;
    }
}
