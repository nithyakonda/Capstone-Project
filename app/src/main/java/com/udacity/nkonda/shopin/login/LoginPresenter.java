package com.udacity.nkonda.shopin.login;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LogPrinter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.udacity.nkonda.shopin.base.BaseState;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.database.ShopinDatabase;
import com.udacity.nkonda.shopin.database.ShopinDatabaseContract;
import com.udacity.nkonda.shopin.util.FirebaseUtils;

public class LoginPresenter implements LoginContract.Presenter {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginContract.View mView;
    private FirebaseAuth mAuth;
    private User mUser;

    public LoginPresenter(LoginContract.View view, FirebaseAuth auth) {
        mView = view;
        mAuth = auth;
    }

    @Override
    public void start(BaseState state) {
        // no op
    }

    @Override
    public BaseState getState() {
        // no op
        return null;
    }

    @Override
    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ShopinDatabase.getInstance();
                            Log.d(TAG, "Login: Success");
                            mView.onLoginSuccess();
                        } else {
                            Log.w(TAG, "Login: Failed with exception: " + task.getException());
                            mView.onLoginFailed(task.getException());
                        }
                    }
                });
    }

    @Override
    public void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Register: Success");
                            if (getCurrentUser() != null) {
                                ShopinDatabase.getInstance().addUser(mUser,
                                        new ShopinDatabaseContract.OnCompletionCallback() {
                                    @Override
                                    public void onResult(boolean success, Exception exception) {
                                        if (success) {
                                            mView.onRegistrationSuccess();
                                        } else {
                                            Log.e(TAG, "Register: Add user to database: Failed with exception: " + exception);
                                            mView.onRegistrationFailed(exception);
                                        }
                                    }
                                });
                            } else {
                                // TODO: 7/21/18 handle null
                            }
                        } else {
                            Log.w(TAG, "Register: Failed with exception: " + task.getException());
                            mView.onRegistrationFailed(task.getException());
                        }
                    }
                });
    }

    @Override
    public void forgotPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Forgot Password: Success");
                            mView.onSendPasswordResetEmailSuccess();
                        } else {
                            Log.w(TAG, "Forgot Password: Failed with exception: " + task.getException());
                            mView.onSendPasswordResetEmailFailed(task.getException());
                        }
                    }
                });
    }

    @Override
    public void updateProfile(String displayName, Uri photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(photoUrl)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Update profile: Success");
                            mView.onUpdateProfileSuccess();
                        } else {
                            Log.w(TAG, "Update profile: Failed with exception: " + task.getException());
                            mView.onUpdateProfileFailed(task.getException());
                        }
                    }
                });
    }

    @Override
    public void logout() {
        mAuth.signOut();
        mView.onLogoutComplete();
    }

    @Override
    public User getCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null && ( mUser == null ||
               !mUser.getEmail().equals(firebaseUser.getEmail()))) {
            mUser = FirebaseUtils.getUser(firebaseUser);
        }
        return mUser;
    }
}
