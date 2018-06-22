package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.base.BaseState;
import com.udacity.nkonda.shopin.data.User;

public class LoginPresenter implements LoginContract.Presenter {

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
    public void login(Context context, String email, String password) {
        mView.onLoginDone(false);
    }

    @Override
    public void register(FirebaseAuth auth, User user, String password) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mView.onRegistrationSuccess();
                        } else {
                            mView.onRegistrationFailed(task.getException());
                        }
                    }
                });
    }

    @Override
    public void forgotPassword(Context context, String email) {
        mView.onSendPasswordResetEmailDone(false);
    }
}
