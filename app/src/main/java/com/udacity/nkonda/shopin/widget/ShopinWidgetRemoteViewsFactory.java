package com.udacity.nkonda.shopin.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ShopinWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ShopinDatabase mDatabase;
    private List<Store> mStores;
    private CountDownLatch mLatch = new CountDownLatch(1);

    public ShopinWidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        ShopinDatabase.getInstance().getStores(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new ShopinDatabaseContract.GetStoresCallback() {
                    @Override
                    public void onResult(boolean success, Exception exception, List<Store> stores) {
                        mLatch.countDown();
                        if (success) {
                            mStores = getStoresWithItemsToBuy(stores);
                        }
                    }
                });
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mStores != null ? mStores.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                position >= mStores.size()) {
            return null;
        }

        Store store = mStores.get(position);
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.layout_widget_list_item);
        rv.setTextViewText(R.id.tv_store_name, store.getName());
        rv.setTextViewText(R.id.tv_item_count, String.valueOf(store.getItemsToBuyCount()));

        Intent fillInIntent = new Intent();
        String storeId = store.getId();
        fillInIntent.putExtra(ShopinWidget.ARG_STORE_ID, storeId);
        rv.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private List<Store> getStoresWithItemsToBuy(List<Store> stores) {
        List<Store> storesWIthItemsToBuy = new ArrayList<>();
        for (Store store : stores) {
            if (store.hasItemsToBuy()) {
                storesWIthItemsToBuy.add(store);
            }
        }
        return storesWIthItemsToBuy;
    }
}
