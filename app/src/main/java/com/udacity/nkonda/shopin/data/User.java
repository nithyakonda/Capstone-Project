package com.udacity.nkonda.shopin.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String mDisplayName;
    private String mEmail;
    private Uri mDisplayPicture;

    private User(){}

    public User(String displayName, String email, Uri displayPicture) {
        this.mDisplayName = displayName;
        this.mEmail = email;
        this.mDisplayPicture = displayPicture;
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

    public static class Builder {
        private String mDisplayName;
        private String mEmail;
        private Uri mDisplayPicture;

        public Builder(String email) {
            mEmail = email;
        }

        public void setDisplayName(String displayName) {
            mDisplayName = displayName;
        }

        public Builder setDisplayPicture(Uri displayPicture) {
            mDisplayPicture = displayPicture;
            return this;
        }

        public User createUser() {
            return new User(mDisplayName, mEmail, mDisplayPicture);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDisplayName);
        dest.writeString(this.mEmail);
        dest.writeParcelable(this.mDisplayPicture, flags);
    }

    protected User(Parcel in) {
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
