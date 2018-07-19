package com.udacity.nkonda.shopin.storelist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.abdularis.civ.AvatarImageView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListActivity extends BaseActivity implements StoreListContract.View{
    private static final String TAG = StoreListActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;

    StoreListPresenter mPresenter;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_avatar)
    AvatarImageView mAvatarView;

    @BindView(R.id.view_no_stores)
    View mNoStoresView;

    @BindView(R.id.store_list_container)
    @Nullable
    View mStoreListContainer;

    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mPresenter = new StoreListPresenter(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(StoreListActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption();
                } else if (isShow) {
                    isShow = false;
                    hideOption();
                }
            }
        });
        mPresenter.load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.store_list_menu, menu);
        hideOption();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(LoginActivity.SHOW_USER_PROFILE, true);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_add_store) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void setupToolbar(String initials, Uri photoUrl) {
        mAvatarView.setText(initials);
    }

    @Override
    public void displayStores(int num) {
        if (num == 0) {
            mNoStoresView.setVisibility(View.VISIBLE);
            mStoreListContainer.setVisibility(View.INVISIBLE);
        } else {
            mNoStoresView.setVisibility(View.INVISIBLE);
            mStoreListContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideOption() {
        MenuItem item = mMenu.findItem(R.id.action_add_store);
        item.setVisible(false);
    }

    private void showOption() {
        MenuItem item = mMenu.findItem(R.id.action_add_store);
        item.setVisible(true);
    }
}
