package com.udacity.nkonda.shopin.login;

import android.content.Context;

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
    public void register(Context context, User user, String password) {
        mView.onRegistrationDone(false);
    }

    @Override
    public void forgotPassword(Context context, String email) {
        mView.onSendPasswordResetEmailDone(false);
    }
}
