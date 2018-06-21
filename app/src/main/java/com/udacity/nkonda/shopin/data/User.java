package com.udacity.nkonda.shopin.data;

public class User {
    private String mFirstname;
    private String mLastName;
    private String mDisplayName;
    private String mEmail;
    private String mDisplayPicture;

    private User(){}

    public User(String firstname, String lastName, String displayName, String email, String displayPicture) {
        this.mFirstname = firstname;
        this.mLastName = lastName;
        this.mDisplayName = displayName;
        this.mEmail = email;
        this.mDisplayPicture = displayPicture;
    }

    public String getFirstname() {
        return mFirstname;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getDisplayPicture() {
        return mDisplayPicture;
    }

    public static class Builder {
        private String mFirstname;
        private String mLastName;
        private String mDisplayName;
        private String mEmail;
        private String mDisplayPicture;

        public Builder(String firstname, String lastName, String email) {
            mFirstname = firstname;
            mLastName = lastName;
            mEmail = email;
        }

        public Builder setDisplayName(String displayName) {
            mDisplayName = displayName;
            return this;
        }

        public Builder setDisplayPicture(String displayPicture) {
            mDisplayPicture = displayPicture;
            return this;
        }

        public User createUser() {
            return new User(mFirstname, mLastName, mDisplayName, mEmail, mDisplayPicture);
        }
    }
}
