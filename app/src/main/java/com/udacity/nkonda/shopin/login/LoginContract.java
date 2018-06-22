package com.udacity.nkonda.shopin.login;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseView;
import com.udacity.nkonda.shopin.data.User;

public interface LoginContract {
    interface View extends BaseView {
        void onLoginDone(boolean isSuccess);
        void onRegistrationSuccess();
        void onRegistrationFailed(Exception exception);
        void onSendPasswordResetEmailDone(boolean isSuccess);
    }

    interface Presenter extends BasePresenter {
        void login(Context context, String email, String password);
        void register(FirebaseAuth auth, User user, String password);
        void forgotPassword(Context context, String email);
    }
}
