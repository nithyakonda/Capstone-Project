package com.udacity.nkonda.shopin.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.itemlist.ItemListActivity;

public class ShopinWidget extends AppWidgetProvider {
    public static final String ARG_STORE_ID = "ARG_STORE_ID";
    RemoteViews mRemoteViews;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
        mRemoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.widget_store_list
        );

        Intent intent = new Intent(context, ShopinWidgetRemoteViewsService.class);
        mRemoteViews.setRemoteAdapter(R.id.lv_widget_store_list, intent);

        Intent clickIntentTemplate = new Intent(context, ItemListActivity.class);
        PendingIntent clickPendingIntentTemplate = PendingIntent.getActivity(context,
                0,
                clickIntentTemplate,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setPendingIntentTemplate(R.id.lv_widget_store_list, clickPendingIntentTemplate);

        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, ShopinWidget.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        sendRefreshBroadcast(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, ShopinWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lv_widget_store_list);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
