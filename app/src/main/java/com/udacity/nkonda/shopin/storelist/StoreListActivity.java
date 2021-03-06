package com.udacity.nkonda.shopin.storelist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.abdularis.civ.AvatarImageView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.data.Item;
import com.udacity.nkonda.shopin.data.Store;
import com.udacity.nkonda.shopin.itemlist.ItemListActivity;
import com.udacity.nkonda.shopin.login.LoginActivity;
import com.udacity.nkonda.shopin.util.UiUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListActivity extends BaseActivity implements StoreListContract.View{
    private static final String TAG = StoreListActivity.class.getSimpleName();
    private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    private static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 2;
    String[] mPermissions = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};

    StoreListPresenter mPresenter;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_avatar)
    @Nullable
    AvatarImageView mAvatarView;

    @BindView(R.id.fab_add_store)
    FloatingActionButton mAddStoreBtn;

    @BindView(R.id.action_container)
    View mActionContainer;

    @BindView(R.id.no_stores_view)
    @Nullable
    View mNoStoresIcon;

    @BindView(R.id.store_list_container)
    @Nullable
    View mStoreListContainer;

    @BindView(R.id.rv_store_list)
    @Nullable
    RecyclerView mStoreListView;

    private Menu mMenu;
    private StoreListAdapter mStoreListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        requestPermission();

        mPresenter = new StoreListPresenter(this, this, mAuth, mGeofencingClient);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(numberOfColumns(),
                StaggeredGridLayoutManager.VERTICAL);
        mStoreListAdapter = new StoreListAdapter();
        mStoreListAdapter.setOnStoreStatusChangedListener(new StoreListAdapter.OnStoreStatusChangedListener() {
            @Override
            public void onStoreSelected(Store store) {
                startItemListActivity(store);
            }

            @Override
            public void onStoreDeleted(String storeId) {
                mPresenter.deleteStore(storeId);
            }

            @Override
            public void onItemEdited(String storeId, Item item) {
                mPresenter.editItem(storeId, item);
            }
        });
        ItemTouchHelper.Callback callback =
                new StoreListAdapter.StoreListItemTouchHelperCallback(mStoreListAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(mStoreListView);
        mStoreListView.setLayoutManager(layoutManager);
        mStoreListView.setAdapter(mStoreListAdapter);
        mStoreListView.addItemDecoration(new StoreListItemDecoration(16));

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

        mAddStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.load();
        showProgress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
            showPlacePicker();
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
                        new Store.Coordinates(place.getLatLng().latitude, place.getLatLng().longitude));

                mPresenter.addStore(newStore);
            } else if (resultCode != RESULT_CANCELED){
                Log.e(TAG, "onActivityResult::error::resultCode" + resultCode);
                UiUtils.showDefaultError(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                    .setTitle(R.string.dialog_permission_required_title)
                                    .setMessage(R.string.dialog_permission_required_msg)
                                    .setPositiveButton(R.string.dialog_permission_required_allow_btn_label, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermission();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.dialog_permission_required_deny_btn_label, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();

                        }
                    }

                }
            }
        }
    }

    @Override
    public void setupToolbar(String initial, Uri photoUri) {
        if (photoUri != null) {
            Bitmap bitmap = UiUtils.getAvatarBitmap(this, photoUri);
            if (bitmap != null) {
                showAvatarImage(bitmap);
                return;
            }
        }
        showAvatarInitial(initial);
    }

    @Override
    public void displayStores(List<Store> stores) {
        hideProgress();
        if (stores.size() == 0) {
            mActionContainer.setVisibility(View.VISIBLE);
            mNoStoresIcon.setVisibility(View.VISIBLE);
            mStoreListContainer.setVisibility(View.INVISIBLE);
            return;
        }
        mActionContainer.setVisibility(View.INVISIBLE); // TODO: 7/22/18 can remove?
        mStoreListContainer.setVisibility(View.VISIBLE);
        mStoreListAdapter.setStores(stores);
    }

    @Override
    public void addItems(Store store) {
        startItemListActivity(store);
        UiUtils.showToast(this, getString(R.string.toast_added_new_store_text) + store.getName());
    }

    private void startItemListActivity(Store store) {
        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra(ItemListActivity.ARG_STORE_ID, store.getId());
        startActivity(intent);
    }

    private void showPlacePicker() {
        if(checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(StoreListActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else {
            requestPermission();
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


    private void showAvatarImage(Bitmap bitmap) {
        if (mAvatarView != null) {
            mAvatarView.setState(AvatarImageView.SHOW_IMAGE);
            mAvatarView.setStrokeWidth(0);
            mAvatarView.setImageBitmap(bitmap);
        }
    }

    private void showAvatarInitial(String displayName) {
        if (mAvatarView != null) {
            mAvatarView.setState(AvatarImageView.SHOW_INITIAL);
            mAvatarView.setStrokeWidth(2);
            mAvatarView.setText(displayName);
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, mPermissions, PERMISSION_REQUEST);
        }
    }
}
