package com.udacity.nkonda.shopin.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Store {
    private String mId;
    private String mName;
    private String mAddress;
    private LatLng mCoordinates;
    private List<String> items;

    public Store(String id, String name, String address, LatLng coordinates) {
        mId = id;
        mName = name;
        mAddress = address;
        mCoordinates = coordinates;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public List<String> getItems() {
        return items;
    }
}
