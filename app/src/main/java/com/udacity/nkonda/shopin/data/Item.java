package com.udacity.nkonda.shopin.data;

public class Item {
    private String mId;
    private String mName;
    private boolean mStatus;

    public Item() {
    }

    public Item(String id, String name, boolean status) {
        mId = id;
        mName = name;
        mStatus = status;
    }

    public Item(String id) {
        mId = id;
        mName = "";
        mStatus = false;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean getStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }
}
