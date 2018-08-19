package com.udacity.nkonda.shopin.geofence;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.itemlist.ItemListActivity;
import com.udacity.nkonda.shopin.storelist.StoreListActivity;
import com.udacity.nkonda.shopin.util.FirebaseUtils;

public class ShopinNotificationManager {
    private static final String TAG = ShopinNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "SHOPIN_NOTIFICATION_CHANNEL";

    private static ShopinNotificationManager sInstance;
    private NotificationCompat.Builder mBuilder;
    private Context mContext;

    public ShopinNotificationManager(Context context) {
        mContext = context;
        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(mContext.getColor(R.color.colorPrimary))
                .setContentTitle(context.getString(R.string.title_notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static ShopinNotificationManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ShopinNotificationManager(context);
        }
        return sInstance;
    }

    public void notify(Store store) {
        if (store.hasItemsToBuy()) {
            int notificationId = (int) System.currentTimeMillis();
            Log.i(TAG, "notify::sending notification for store " + store.getId());
            String message = String.format(mContext.getString(R.string.message_notification), store.getName());
            mBuilder.setContentText(message)
                    .setContentIntent(getPendingIntent(store.getId()));

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

    private PendingIntent getPendingIntent(String storeId) {
        Intent itemListIntent = new Intent(mContext, ItemListActivity.class);
        itemListIntent.putExtra(ItemListActivity.ARG_STORE_ID, storeId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntent(itemListIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return resultPendingIntent;
    }
}
