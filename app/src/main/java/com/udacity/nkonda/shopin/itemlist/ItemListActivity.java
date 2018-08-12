package com.udacity.nkonda.shopin.itemlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.data.Item;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemListActivity extends BaseActivity
        implements ItemListContract.View {
    public static final String ARG_STORE_ID = "ARG_STORE_ID";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.rv_item_list)
    RecyclerView mItemListView;

    @BindView(R.id.tv_store_address)
    @Nullable
    TextView mAddressView;

    private ItemListAdapter mAdapter;
    private ItemListState mState;
    private ItemListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            mState = new ItemListState(getIntent().getStringExtra(ARG_STORE_ID));
        } else {
            mState = new ItemListState(savedInstanceState.getString(ARG_STORE_ID));
        }
        mPresenter = new ItemListPresenter(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mAdapter = new ItemListAdapter(mState.getStoreId(), new ItemListAdapter.OnItemUpdateListener() {
            @Override
            public void onItemAdded(Item item) {
                mPresenter.addItem(item);
            }

            @Override
            public void onItemEdited(Item item) {
                mPresenter.editItem(item);
            }

            @Override
            public void onItemDeleted(Item item) {
                mPresenter.deleteItem(item);
            }

            @Override
            public void focusNext(int pos) {
                View v = layoutManager.findViewByPosition(pos + 1);
                if (v != null) {
                    v.requestFocus();
                }
            }

            @Override
            public void focusPrevious(int pos) {
                View v = layoutManager.findViewByPosition(pos > 0 ? pos - 1 : 0);
                if (v != null) {
                    v.requestFocus();
                }
            }
        });


        mItemListView.setLayoutManager(layoutManager);
        mItemListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideKeyboard();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start(mState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_STORE_ID, mPresenter.getState().getStoreId());
    }

    @Override
    public void setupToolbar(String name, String address) {
        mToolbar.setTitle(name);
        mAddressView.setText(address);
    }

    @Override
    public void displayItems(List<Item> items) {
        mAdapter.setItems(items);
    }

    private void hideKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
