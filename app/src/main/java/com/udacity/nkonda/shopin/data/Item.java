package com.udacity.nkonda.shopin.data;

public class Item {
    private String mName;
    private boolean mStatus;

    public Item(String name, boolean status) {
        mName = name;
        mStatus = status;
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
