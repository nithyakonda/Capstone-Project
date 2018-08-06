package com.udacity.nkonda.shopin.itemlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>{
    private static final String PLACEHOLDER_ITEM_NAME = "PLACEHOLDER_ITEM_NAME";
    private ArrayList<Item> mItems;
    private Context mContext;

    public void setItems(ArrayList<Item> items) {
        mItems = items;
        mItems.add(new Item(PLACEHOLDER_ITEM_NAME, false));
    }

    @NonNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_list_item, parent, false);
        return new ItemListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListViewHolder holder, int position) {
        if (mItems.get(position).getName() != PLACEHOLDER_ITEM_NAME) {
            Item item = mItems.get(position);
            holder.bindEditItemView(item.getName(), item.getStatus());
        } else {
            holder.bindAddItemView();
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    class ItemListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            TextView.OnEditorActionListener,
            View.OnTouchListener,
            CompoundButton.OnCheckedChangeListener,
            View.OnFocusChangeListener{
        @BindView(R.id.et_add_item)
        @Nullable
        EditText mAddItemView;

        @BindView(R.id.cb_item_status)
        @Nullable
        CheckBox mItemStatusView;

        @BindView(R.id.et_edit_item)
        @Nullable
        EditText mEditItemView;

        @BindView(R.id.btn_delete_item)
        @Nullable
        ImageButton mDeleteItemBtn;

        @BindView(R.id.add_item_container)
        @Nullable
        View mAddItemContainer;

        @BindView(R.id.edit_item_container)
        @Nullable
        View mEditItemContainer;

        InputMethodManager mKeyboardMgr;

        public ItemListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mKeyboardMgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            itemView.setOnClickListener(this);
            mAddItemView.setOnTouchListener(this);
            mEditItemView.setOnEditorActionListener(this);
            mEditItemView.setOnTouchListener(this);
            mEditItemView.setOnFocusChangeListener(this);
            mDeleteItemBtn.setOnClickListener(this);
            mItemStatusView.setOnCheckedChangeListener(this);
        }

        void bindAddItemView() {
            showAddItemLayout();
        }

        void bindEditItemView(String item, boolean status) {
            showEditItemLayout();
            mItemStatusView.setChecked(status);
            mEditItemView.setText(item);
            if (item.isEmpty()) {
                mEditItemView.requestFocus();
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v.getId() == R.id.et_add_item) {
                    int insertPos = mItems.size() - 1; // because last one is a placeholder
                    addNewItem(insertPos);
                } else if (v.getId() == R.id.et_edit_item) {
                    mDeleteItemBtn.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v.getId() == R.id.btn_delete_item) {
                mItems.remove(pos);
                notifyItemRemoved(pos);
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.et_edit_item && hasFocus) {
                mDeleteItemBtn.setVisibility(View.VISIBLE);
                mKeyboardMgr.toggleSoftInput(0, 0);
            } else {
                editItem((EditText) v);
                mDeleteItemBtn.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int pos = getAdapterPosition();
            mItems.get(pos).setStatus(isChecked);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // TODO: 7/29/18 handle backpress
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    int pos = getAdapterPosition();
                    editItem((EditText) v);
                    addNewItem(pos + 1);
                    return true;
                }
            }
            return false;
        }

        private void showAddItemLayout() {
            mEditItemContainer.setVisibility(View.INVISIBLE);
            mAddItemContainer.setVisibility(View.VISIBLE);
        }

        private void showEditItemLayout() {
            mEditItemContainer.setVisibility(View.VISIBLE);
            mAddItemContainer.setVisibility(View.INVISIBLE);
        }

        private void addNewItem(int pos) {
            mItems.add(pos, new Item("", false));
            // itemCount = from changed pos until end of list
            notifyItemRangeChanged(pos, mItems.size() - pos);
        }

        private void editItem(EditText v) {
            int pos = getAdapterPosition();
            if (pos >= 0 && mItems.get(pos).getName() != PLACEHOLDER_ITEM_NAME) {
                mItems.get(pos).setName(v.getText().toString());
            }
        }
    }
}
