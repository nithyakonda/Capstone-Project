package com.udacity.nkonda.shopin.storelist;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder> {
    private Context mContext;
    private List<Store> mStores; // TODO: 7/22/18 consider making it a Set
    private OnItemSelectedListener mOnItemSelectedListener;

    public void setStores(List<Store> stores) {
        mStores = stores;
        notifyDataSetChanged();
    }

    public void addStore(Store store) {
        mStores.add(store);
        notifyItemInserted(mStores.size() - 1);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    @NonNull
    @Override
    public StoreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_store_list_item, parent, false);
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

        @BindView(R.id.lv_item_list)
        ListView mItemListView;

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
            mStoreNameView.setText(store.getName());
            mItemListView.setAdapter(new ArrayAdapter<String>(context,
                    R.layout.layout_item_list_item,
                    mItems));
            mItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mOnItemSelectedListener.onItemSelected(mItems.get(position));
                }
            });
        }

        @Override
        public void onClick(View v) {
            Store selectedStore = mStores.get(getAdapterPosition());
            mOnItemSelectedListener.onStoreSelected(selectedStore);
        }
    }

    public interface OnItemSelectedListener {
        public void onStoreSelected(Store store);
        public void onItemSelected(String item);
    }
}
