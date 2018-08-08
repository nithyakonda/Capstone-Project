package com.udacity.nkonda.shopin.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private String mId;
    private String mName;
    private String mAddress;
    private Coordinates mCoordinates;
//    private List<Item> mItems;
    private Map<String,Item> mItems;

    public Store() {
    }

    public Store(String id, String name, String address, Coordinates coordinates) {
        mId = id;
        mName = name;
        mAddress = address;
        mCoordinates = coordinates;
        mItems = new HashMap<>();
    }

    public Store(String id, String name, String address, Coordinates coordinates, Map<String, Item> items) {
        mId = id;
        mName = name;
        mAddress = address;
        mCoordinates = coordinates;
        mItems = items;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setCoordinates(Coordinates coordinates) {
        mCoordinates = coordinates;
    }

    public void setItems(Map<String, Item> items) {
        mItems = items;
    }

    public void setItems(List<Item> items) {
        mItems.clear();
        for (Item item : items) {
            mItems.put(item.getId(), item);
        }
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

    public Coordinates getCoordinates() {
        return mCoordinates;
    }

    public List<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        if (mItems != null) {
            items =  new ArrayList<Item>(mItems.values());
        }
        return items;
    }

    public static class Coordinates {
        public double latitude;
        public double longitude;

        public Coordinates() {
        }

        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Coordinates(com.google.android.gms.maps.model.LatLng coordinates) {
            new Coordinates(coordinates.latitude, coordinates.longitude);
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
