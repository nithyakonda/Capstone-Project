package com.udacity.nkonda.shopin.data;


import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Store {
    private String mId;
    private String mName;
    private String mAddress;
    private Coordinates mCoordinates;
    private ArrayList<Item> mItems;

    public Store() {
    }

    public Store(String id, String name, String address, Coordinates coordinates) {
        mId = id;
        mName = name;
        mAddress = address;
        mCoordinates = coordinates;
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

    public void setItems(ArrayList<Item> items) {
        this.mItems = items;
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

    public ArrayList<Item> getItems() {
        return mItems;
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
