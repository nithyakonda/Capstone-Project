package com.udacity.nkonda.shopin.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.base.BaseActivity;

import butterknife.BindView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements
        LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener, ForgotPasswordFragment.OnFragmentInteractionListener {

    // UI references.
    @BindView(R.id.login_progress)
    @Nullable
    private View mProgressView;

    @BindView(R.id.form_container)
    private View mFormContainerView;

    @BindView(R.id.btn_cancel)
    @Nullable
    private ImageButton mCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            replaceFormContainerWith(new LoginFragment());
        }

        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFormContainerWith(new LoginFragment());
                mCancelBtn.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onLoginDone() {

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
    public void onRegistrationDone(Uri uri) {

    }

    @Override
    public void onPasswordSent(Uri uri) {

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

