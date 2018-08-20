package com.udacity.nkonda.shopin.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ShopinWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ShopinWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}
