package com.udacity.nkonda.shopin.itemlist;

import com.udacity.nkonda.shopin.base.BaseState;
import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;

import java.util.ArrayList;
import java.util.List;

public class ItemListState implements BaseState {
    private String mStoreId;
    private List<Item> mItems;

    public ItemListState(String storeId) {
        mStoreId = storeId;
    }

    public ItemListState(String storeId, List<Item> items) {
        mStoreId = storeId;
        mItems = items;
    }

    public String getStoreId() {
        return mStoreId;
    }

    public List<Item> getItems() {
        return mItems;
    }
}
