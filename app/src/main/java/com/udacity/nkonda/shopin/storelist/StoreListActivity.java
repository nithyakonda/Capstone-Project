package com.udacity.nkonda.shopin.storelist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.login.LoginActivity;
import com.udacity.nkonda.shopin.util.Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListActivity extends BaseActivity implements StoreListContract.View{
    private static final String TAG = StoreListActivity.class.getSimpleName();
    public static final String ARG_USER = "ARG_USER";
    private static final int PLACE_PICKER_REQUEST = 1;

    StoreListPresenter mPresenter;
    StoreListState mState;

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

        if (savedInstanceState == null) {
            if (getIntent().hasExtra(ARG_USER))
                mState = new StoreListState((User) getIntent().getParcelableExtra(ARG_USER));
        } else {
            mState = new StoreListState((User) savedInstanceState.getParcelable(ARG_USER));
        }
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start(mState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_USER, mPresenter.getState().getUser());
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

                Store newStore = new Store(place.getId(),
                        place.getName().toString(),
                        place.getAddress().toString(),
                        place.getLatLng());

                mPresenter.addNewStoreAndCreateGeofence(newStore);
            } else {
                Log.e(TAG, "onActivityResult::error::resultCode" + resultCode);
                Utils.showDefaultError(this);
            }
        }
    }

    @Override
    public void setupToolbar(String initial, Uri photoUri) {
        if (photoUri != null) {
            showAvatarImage(photoUri);
        } else {
            showAvatarInitial(initial);
        }
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

    @Override
    public void displayNewStore(Store store) {
        Utils.showToast(this, "Added " + store.getName());
    }

    private void hideOption() {
        MenuItem item = mMenu.findItem(R.id.action_add_store);
        item.setVisible(false);
    }

    private void showOption() {
        MenuItem item = mMenu.findItem(R.id.action_add_store);
        item.setVisible(true);
    }


    private void showAvatarImage(Uri photoUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            mAvatarView.setState(AvatarImageView.SHOW_IMAGE);
            mAvatarView.setStrokeWidth(0);
            mAvatarView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showDefaultError(this);
        }
    }

    private void showAvatarInitial(String displayName) {
        mAvatarView.setState(AvatarImageView.SHOW_INITIAL);
        mAvatarView.setStrokeWidth(2);
        mAvatarView.setText(displayName);
    }
}
