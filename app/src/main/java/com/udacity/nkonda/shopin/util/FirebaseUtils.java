package com.udacity.nkonda.shopin.util;

import com.google.firebase.auth.FirebaseUser;
import com.udacity.nkonda.shopin.data.User;

public class FirebaseUtils {
    public static User getUser(FirebaseUser firebaseUser) {
        return new User(firebaseUser.getUid(),
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(), firebaseUser.getPhotoUrl());
    }
}
