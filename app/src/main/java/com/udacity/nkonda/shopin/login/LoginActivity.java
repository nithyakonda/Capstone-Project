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
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.storelist.StoreListActivity;
import com.udacity.nkonda.shopin.util.Utils;

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

    public static final String SHOW_USER_PROFILE = "SHOW_USER_PROFILE";

    // UI references.
    @BindView(R.id.login_progress)
    @Nullable
    View mProgressView;

    @BindView(R.id.intro_container)
    View mIntroContainer;

    @BindView(R.id.form_container)
    View mFormContainerView;

    @BindView(R.id.btn_cancel)
    @Nullable
    ImageButton mCancelBtn;

    private LoginContract.Presenter mPresenter;
    private boolean isLoggedIn; // TODO: 7/17/18 add to state

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
        }

        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7/17/18 consider popping from backstack instead of remembering the login state
                if (!isLoggedIn) {
                    replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
                } else {
                    Intent intent = new Intent(LoginActivity.this, StoreListActivity.class);
                    startActivity(intent);
                }
                mCancelBtn.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVEKEY_CANCEL_BUTTON_VISIBLE, mCancelBtn.getVisibility());
    }


    // Login
    @Override
    public void onUserCredentialsCaptured(String email, String password) {
        showProgress(true);
        mPresenter.login(email, password);
    }

    @Override
    public void onLoginSuccess() {
        showProgress(false);
        isLoggedIn = true;
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFailed(Exception exception) {
        showProgress(false);
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
    public void onNewUserInfoCaptured(User user, String password) {
        showProgress(true);
        mPresenter.register(user, password);
    }

    @Override
    public void onRegistrationSuccess() {
        isLoggedIn = true;
        mCancelBtn.setVisibility(View.VISIBLE);
        showProgress(false);
        replaceFormContainerWith(ProfileFragment.newInstance(true,
                mPresenter.getCurrentUser()), UPDATE_PROFILE_FRAGMENT_TAG);
    }

    @Override
    public void onRegistrationFailed(Exception exception) {
        showProgress(false);
        RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            fragment.showPasswordError(getString(R.string.error_weak_password_exception));
        } catch(FirebaseAuthUserCollisionException e) {
            fragment.showEmailError(getString(R.string.error_user_collision_exception));
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            Utils.showToast(this, e.getMessage());
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
        showProgress(true);
        mPresenter.forgotPassword(email);
    }

    @Override
    public void onSendPasswordResetEmailSuccess() {
        showProgress(false);
        Utils.showAlert(this,
                getString(R.string.dialog_title_done),
                getString(R.string.dialog_msg_reset_email_sent));
    }

    @Override
    public void onSendPasswordResetEmailFailed(Exception exception) {
        showProgress(false);
        ForgotPasswordFragment fragment = (ForgotPasswordFragment) getSupportFragmentManager().findFragmentByTag(FORGOT_PASSWORD_FRAGMENT_TAG);
        try {
            throw exception;
        } catch (FirebaseAuthInvalidCredentialsException e) {
            fragment.showError(getString(R.string.error_invalid_email_exception));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Utils.showToast(this, e.getMessage());
        }
    }

    // Update Profile
    @Override
    public void onProfileInfoCaptured(String displayName, Uri photoUrl) {
        showProgress(true);
        mPresenter.updateProfile(displayName, photoUrl);
    }

    @Override
    public void onUpdateProfileSuccess() {
        showProgress(false);
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUpdateProfileFailed(Exception exception) {
        showProgress(false);
        Utils.showDefaultError(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LoginActivity.this, StoreListActivity.class);
                startActivity(intent);
            }
        });
    }

    // Logout
    @Override
    public void onLogout() {
        showProgress(true);
        mPresenter.logout();
    }

    @Override
    public void onLogoutComplete() {
        showProgress(false);
        isLoggedIn = false;
        replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormContainerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
}

