package com.udacity.nkonda.shopin.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String mUid;
    private String mDisplayName;
    private String mEmail;
    private Uri mDisplayPicture;

    public User(String uid, String displayName, String email, Uri displayPicture) {
        mUid = uid;
        mDisplayName = displayName;
        mEmail = email;
        mDisplayPicture = displayPicture;
    }

    public String getUid() {
        return mUid;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getEmail() {
        return mEmail;
    }

    public Uri getDisplayPicture() {
        return mDisplayPicture;
    }

    public void setDisplayPicture(Uri displayPicture) {
        mDisplayPicture = displayPicture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUid);
        dest.writeString(this.mDisplayName);
        dest.writeString(this.mEmail);
        dest.writeParcelable(this.mDisplayPicture, flags);
    }

    protected User(Parcel in) {
        this.mUid = in.readString();
        this.mDisplayName = in.readString();
        this.mEmail = in.readString();
        this.mDisplayPicture = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
