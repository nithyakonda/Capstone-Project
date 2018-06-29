package com.udacity.nkonda.shopin.login;

import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.base.BasePresenter;
import com.udacity.nkonda.shopin.base.BaseView;
import com.udacity.nkonda.shopin.data.User;

public interface LoginContract {
    interface View extends BaseView {
        void onLoginSuccess();
        void onLoginFailed(Exception exception);
        void onRegistrationSuccess();
        void onRegistrationFailed(Exception exception);
        void onSendPasswordResetEmailSuccess();
        void onSendPasswordResetEmailFailed(Exception exception);
    }

    interface Presenter extends BasePresenter {
        void login(FirebaseAuth auth, String email, String password);
        void register(FirebaseAuth auth, User user, String password);
        void forgotPassword(FirebaseAuth auth, String email);
    }
}
