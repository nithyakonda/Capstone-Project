package com.udacity.nkonda.shopin.storelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder> {
    private Context mContext;
    private List<Store> mStores = new ArrayList<>(); // TODO: 7/22/18 consider making it a Set
    private OnStoreStatusChangedListener mOnStoreStatusChangedListener;

    public void setStores(List<Store> stores) {
        mStores = stores;
        notifyDataSetChanged();
    }

    public void addStore(Store store) {
        mStores.add(store);
        notifyItemInserted(mStores.size() - 1);
    }

    public void setOnStoreStatusChangedListener(OnStoreStatusChangedListener onStoreStatusChangedListener) {
        mOnStoreStatusChangedListener = onStoreStatusChangedListener;
    }

    void onItemDismiss(int position) {
        mOnStoreStatusChangedListener.onStoreDeleted(mStores.get(position).getId());
        mStores.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public StoreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_store_list_item, null, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new StoreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreListViewHolder holder, int position) {
        holder.bind(mContext, mStores.get(position));
    }

    @Override
    public int getItemCount() {
        return mStores == null ? 0 : mStores.size();
    }

    public class StoreListViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        @BindView(R.id.tv_store_name)
        TextView mStoreNameView;

        @BindView(R.id.item_list_container)
        LinearLayout mItemListContainer;

        private List<String> mItems = new ArrayList<>();

        public StoreListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(Context context, final Store store) {
            if (store.getItems() != null) {
                mItems = new ArrayList<>(store.getItems().keySet());
            }
            mStoreNameView.setText(store.getName()); // TODO: 7/28/18 if name is null, coordinates are displayed
            for (Map.Entry<String, Boolean> item : store.getItems().entrySet()) {
                final CheckBox itemView = new CheckBox(context);
                itemView.setText(item.getKey());
                itemView.setChecked(item.getValue());

                itemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mOnStoreStatusChangedListener.onItemUpdated(itemView.getText().toString(), itemView.isChecked());
                    }
                });
                mItemListContainer.addView(itemView);
            }
        }

        @Override
        public void onClick(View v) {
            Store selectedStore = mStores.get(getAdapterPosition());
            mOnStoreStatusChangedListener.onStoreSelected(selectedStore);
        }
    }

    public static class StoreListItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final StoreListAdapter mAdapter;

        public StoreListItemTouchHelperCallback(StoreListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder,
                ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

    }

    public interface OnStoreStatusChangedListener {
        public void onStoreSelected(Store store);
        public void onStoreDeleted(String storeId);
        public void onItemUpdated(String item, boolean status);
    }
}
