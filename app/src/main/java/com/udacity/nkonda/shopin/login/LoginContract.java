package com.udacity.nkonda.shopin.login;

import android.content.Context;

import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseView;
import com.udacity.nkonda.shopin.data.User;

public interface LoginContract {
    interface View extends BaseView {
        void onLoginDone(boolean isSuccess);
        void onRegistrationDone(boolean isSuccess);
        void onSendPasswordResetEmailDone(boolean isSuccess);
    }

    interface Presenter extends BasePresenter {
        void login(Context context, String email, String password);
        void register(Context context, User user, String password);
        void forgotPassword(Context context, String email);
    }
}
