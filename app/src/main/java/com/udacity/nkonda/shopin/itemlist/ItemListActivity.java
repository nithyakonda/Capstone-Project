package com.udacity.nkonda.shopin.itemlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.data.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemListActivity extends BaseActivity
        implements ItemListContract.View {

    @BindView(R.id.rv_item_list)
    RecyclerView mItemListView;

    private ItemListAdapter mAdapter;

    private LinkedHashMap<String, Boolean> dummyItems = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mAdapter = new ItemListAdapter();

        final ArrayList<Item> dummyItems1 = new ArrayList<>();
        dummyItems1.add(new Item("Eggs", true));
        dummyItems1.add(new Item("Milk", false));
        dummyItems1.add(new Item("Bread", false));

        mAdapter.setItems(dummyItems1);

        mItemListView.setLayoutManager(layoutManager);
        mItemListView.setAdapter(mAdapter);
    }
}
