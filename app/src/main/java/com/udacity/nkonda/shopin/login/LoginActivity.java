package com.udacity.nkonda.shopin.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.storelist.StoreListActivity;
import com.udacity.nkonda.shopin.util.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements
        LoginContract.View,
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        ForgotPasswordFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String LOGIN_FRAGMENT_TAG = LoginFragment.class.getSimpleName();
    private static final String REGISTER_FRAGMENT_TAG = RegisterFragment.class.getSimpleName();
    private static final String FORGOT_PASSWORD_FRAGMENT_TAG = ForgotPasswordFragment.class.getSimpleName();
    private static final String UPDATE_PROFILE_FRAGMENT_TAG = ProfileFragment.class.getSimpleName();

    private static final String SAVEKEY_CANCEL_BUTTON_VISIBLE = "SAVEKEY_CANCEL_BUTTON_VISIBLE";
    private static final String SAVEKEY_IS_LOGGED_IN = "SAVEKEY_IS_LOGGED_IN";

    public static final String SHOW_USER_PROFILE = "SHOW_USER_PROFILE";

    // UI references.

    @BindView(R.id.intro_container)
    View mIntroContainer;

    @BindView(R.id.form_container)
    View mFormContainerView;

    @BindView(R.id.btn_cancel)
    @Nullable
    ImageButton mCancelBtn;

    private LoginContract.Presenter mPresenter;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mPresenter = new LoginPresenter(this, mAuth);

        if (savedInstanceState == null) {
            if (getIntent().hasExtra(SHOW_USER_PROFILE) && getIntent().getBooleanExtra(SHOW_USER_PROFILE, false)) {
                isLoggedIn = true;
                replaceFormContainerWith(ProfileFragment.newInstance(false,
                        mPresenter.getCurrentUser()),
                        UPDATE_PROFILE_FRAGMENT_TAG);
                mCancelBtn.setVisibility(View.VISIBLE);
            } else {
                replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
            }
        } else {
            mCancelBtn.setVisibility(savedInstanceState.getInt(SAVEKEY_CANCEL_BUTTON_VISIBLE));
            isLoggedIn = savedInstanceState.getBoolean(SAVEKEY_IS_LOGGED_IN);
        }

        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoggedIn) {
                    replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
                } else {
                    startStoreListActivity();
                }
                mCancelBtn.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVEKEY_CANCEL_BUTTON_VISIBLE, mCancelBtn.getVisibility());
        outState.putBoolean(SAVEKEY_IS_LOGGED_IN, isLoggedIn);
    }


    // Login
    @Override
    public void onUserCredentialsCaptured(String email, String password) {
        showProgress();
        mPresenter.login(email, password);
    }

    @Override
    public void onLoginSuccess() {
        hideProgress();
        isLoggedIn = true;
        startStoreListActivity();
    }

    @Override
    public void onLoginFailed(Exception exception) {
        hideProgress();
        LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);
        try {
            throw exception;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            fragment.showLoginError(getString(R.string.error_invalid_login_credentials_exception));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            fragment.showLoginError(e.getMessage());
        }
    }

    // Register
    @Override
    public void onNewRegistration() {
        mCancelBtn.setVisibility(View.VISIBLE);
        replaceFormContainerWith(new RegisterFragment(), REGISTER_FRAGMENT_TAG);
    }

    @Override
    public void onNewUserInfoCaptured(String email, String password) {
        showProgress();
        mPresenter.register(email, password);
    }

    @Override
    public void onRegistrationSuccess() {
        isLoggedIn = true;
        mCancelBtn.setVisibility(View.VISIBLE);
        hideProgress();
        replaceFormContainerWith(ProfileFragment.newInstance(true,
                mPresenter.getCurrentUser()), UPDATE_PROFILE_FRAGMENT_TAG);
    }

    @Override
    public void onRegistrationFailed(Exception exception) {
        hideProgress();
        RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            fragment.showPasswordError(getString(R.string.error_weak_password_exception));
        } catch(FirebaseAuthUserCollisionException e) {
            fragment.showEmailError(getString(R.string.error_user_collision_exception));
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            UiUtils.showToast(this, e.getMessage());
        }
    }

    // Forgot Password
    @Override
    public void onForgotPasssword() {
        mCancelBtn.setVisibility(View.VISIBLE);
        replaceFormContainerWith(new ForgotPasswordFragment(), FORGOT_PASSWORD_FRAGMENT_TAG);
    }

    @Override
    public void onPasswordResetEmailCaptured(String email) {
        showProgress();
        mPresenter.forgotPassword(email);
    }

    @Override
    public void onSendPasswordResetEmailSuccess() {
        hideProgress();
        UiUtils.showAlert(this,
                getString(R.string.dialog_title_done),
                getString(R.string.dialog_msg_reset_email_sent));
    }

    @Override
    public void onSendPasswordResetEmailFailed(Exception exception) {
        hideProgress();
        ForgotPasswordFragment fragment = (ForgotPasswordFragment) getSupportFragmentManager().findFragmentByTag(FORGOT_PASSWORD_FRAGMENT_TAG);
        try {
            throw exception;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            fragment.showError(getString(R.string.error_invalid_email_exception));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            UiUtils.showToast(this, e.getMessage());
        }
    }

    // Update Profile
    @Override
    public void onProfileInfoCaptured(String displayName, Uri photoUrl) {
        showProgress();
        mPresenter.updateProfile(displayName, photoUrl);
    }

    @Override
    public void onUpdateProfileSuccess() {
        hideProgress();
        startStoreListActivity();
    }

    @Override
    public void onUpdateProfileFailed(Exception exception) {
        hideProgress();
        UiUtils.showDefaultError(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startStoreListActivity();
            }
        });
    }

    // Logout
    @Override
    public void onLogout() {
        showProgress();
        mPresenter.logout();
    }

    @Override
    public void onLogoutComplete() {
        hideProgress();
        isLoggedIn = false;
        replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
    }

    private void replaceFormContainerWith(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(
                R.id.form_container,
                fragment,
                tag
        );
        fragmentTransaction.commit();
    }

    private void startStoreListActivity() {
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
    }
}

