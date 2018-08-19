package com.udacity.nkonda.shopin.itemlist;

import android.os.Bundle;
import android.os.Handler;
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
    private LinearLayoutManager mLayoutManager;

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

        mLayoutManager = new LinearLayoutManager(this,
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
                requestFocusAt(pos + 1);
            }

            @Override
            public void focusPrevious(int pos) {
                requestFocusAt(pos > 0 ? pos - 1 : 0);
            }
        });


        mItemListView.setLayoutManager(mLayoutManager);
        mItemListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveCurrentItemState();
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
        showProgress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCurrentItemState();
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
        hideProgress();
    }

    private void requestFocusAt(int pos) {
        View v = mLayoutManager.findViewByPosition(pos);
        if (v != null) {
            v.requestFocus();
        }
    }

    private void saveCurrentItemState() {
        ItemListAdapter.ItemListViewHolder vh = (ItemListAdapter.ItemListViewHolder) mItemListView.
                findViewHolderForAdapterPosition(mAdapter.getCurrentPos());
        if (vh != null) {
            vh.saveState();
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if(view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            });
        }
    }
}
