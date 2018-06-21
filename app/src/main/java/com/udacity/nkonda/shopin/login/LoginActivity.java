package com.udacity.nkonda.shopin.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;
import com.udacity.nkonda.shopin.data.User;
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
            replaceFormContainerWith(new LoginFragment());
        }

        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFormContainerWith(new LoginFragment());
                mCancelBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onNewRegistration() {
        mCancelBtn.setVisibility(View.VISIBLE);
        replaceFormContainerWith(new RegisterFragment());
    }

    @Override
    public void onForgotPasssword() {
        mCancelBtn.setVisibility(View.VISIBLE);
        replaceFormContainerWith(new ForgotPasswordFragment());
    }

    @Override
    public void onUserCredentialsCaptured(String email, String password) {
        showProgress(true);
        mPresenter.login(this, email, password);
    }

    @Override
    public void onNewUserInfoCaptured(User user, String password) {
        showProgress(true);
        mPresenter.register(this, user, password);
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
    }

    @Override
    public void onRegistrationDone(boolean isSuccess) {
        showProgress(false);
        Utils.showToast(this, "Registration Done");
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

    private void replaceFormContainerWith(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(
                R.id.form_container,
                fragment
        );
        fragmentTransaction.commit();
    }
}

