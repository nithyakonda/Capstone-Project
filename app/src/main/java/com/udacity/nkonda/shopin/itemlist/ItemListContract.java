package com.udacity.nkonda.shopin.itemlist;

import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseView;
import com.udacity.nkonda.shopin.data.Item;

import java.util.ArrayList;
import java.util.List;

public interface ItemListContract {
    interface View extends BaseView {
        void setupToolbar(String name, String address);
        void displayItems(List<Item> items);
    }

    interface Presenter extends BasePresenter<ItemListState> {
        void load();
        void addItem(Item item);
        void deleteItem(Item item);
        void editItem(Item item);
    }
}
