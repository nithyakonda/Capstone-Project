package com.udacity.nkonda.shopin.login;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.base.BaseState;
import com.udacity.nkonda.shopin.data.User;

public class LoginPresenter implements LoginContract.Presenter {
    private static final String TAG = "Login";

    private LoginContract.View mView;

    public LoginPresenter(LoginContract.View view) {
        mView = view;
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
    public void login(FirebaseAuth auth, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
    public void register(FirebaseAuth auth, User user, String password) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Register: Success");
                            mView.onRegistrationSuccess();
                        } else {
                            Log.w(TAG, "Register: Failed with exception: " + task.getException());
                            mView.onRegistrationFailed(task.getException());
                        }
                    }
                });
    }

    @Override
    public void forgotPassword(FirebaseAuth auth, String email) {
        auth.sendPasswordResetEmail(email)
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
        mView.onSendPasswordResetEmailSuccess();
    }
}
