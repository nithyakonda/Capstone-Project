package com.udacity.nkonda.shopin.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
        ForgotPasswordFragment.OnFragmentInteractionListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String LOGIN_FRAGMENT_TAG = LoginFragment.class.getSimpleName();
    private static final String REGISTER_FRAGMENT_TAG = RegisterFragment.class.getSimpleName();
    private static final String FORGOT_PASSWORD_FRAGMENT_TAG = ForgotPasswordFragment.class.getSimpleName();

    // UI references.
    @BindView(R.id.login_progress)
    @Nullable
    View mProgressView;

    @BindView(R.id.form_container)
    View mFormContainerView;

    @BindView(R.id.btn_cancel)
    @Nullable
    ImageButton mCancelBtn;

    private LoginContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPresenter = new LoginPresenter(this);

        if (savedInstanceState == null) {
            replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
        }

        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFormContainerWith(new LoginFragment(), LOGIN_FRAGMENT_TAG);
                mCancelBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onNewRegistration() {
        mCancelBtn.setVisibility(View.VISIBLE);
        replaceFormContainerWith(new RegisterFragment(), REGISTER_FRAGMENT_TAG);
    }

    @Override
    public void onForgotPasssword() {
        mCancelBtn.setVisibility(View.VISIBLE);
        replaceFormContainerWith(new ForgotPasswordFragment(), FORGOT_PASSWORD_FRAGMENT_TAG);
    }

    @Override
    public void onUserCredentialsCaptured(String email, String password) {
        showProgress(true);
        mPresenter.login(this, email, password);
    }

    @Override
    public void onNewUserInfoCaptured(User user, String password) {
        showProgress(true);
        mPresenter.register(mAuth, user, password);
    }

    @Override
    public void onPasswordResetEmailCaptured(String email) {
        showProgress(true);
        mPresenter.forgotPassword(this, email);
    }

    @Override
    public void onLoginDone(boolean isSuccess) {
        showProgress(false);
        Utils.showToast(this, "Login Done");
        if (isSuccess) {
            Intent intent = new Intent(this, StoreListActivity.class);
            startActivity(intent);
        } else {
            LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);
            fragment.showLoginError();
        }
    }

    @Override
    public void onRegistrationSuccess() {
        showProgress(false);
        Intent intent = new Intent(this, StoreListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRegistrationFailed(Exception exception) {
        showProgress(false);
        RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            fragment.showPasswordError(e.getMessage());
        } catch(FirebaseAuthUserCollisionException e) {
            fragment.showEmailError(e.getMessage());
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            Utils.showToast(this, e.getMessage());
        }
    }

    @Override
    public void onSendPasswordResetEmailDone(boolean isSuccess) {
        showProgress(false);
        Utils.showToast(this, "Password Reset Done");
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

