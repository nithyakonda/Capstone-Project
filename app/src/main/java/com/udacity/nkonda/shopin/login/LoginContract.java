package com.udacity.nkonda.shopin.login;

import android.net.Uri;

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
        void onUpdateProfileSuccess();
        void onUpdateProfileFailed(Exception exception);
        void onLogoutComplete();
    }

    interface Presenter extends BasePresenter {
        void login(String email, String password);
        void register(String email, String password);
        void forgotPassword(String email);
        void updateProfile(String displayName, Uri photoUrl);
        void logout();
        User getCurrentUser();
    }
}
