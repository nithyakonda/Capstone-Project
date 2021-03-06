package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.util.SharedPrefUtils;
import com.udacity.nkonda.shopin.util.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.et_email)
    EditText mEmailView;

    @BindView(R.id.et_password)
    EditText mPasswordView;

    @BindView(R.id.password_container)
    TextInputLayout mPasswordContainer;

    private OnFragmentInteractionListener mListener;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        mEmailView.requestFocus();
        if (SharedPrefUtils.contains(getActivity(), SharedPrefUtils.PREF_LOGIN_EMAIL)) {
            mEmailView.setText(SharedPrefUtils.get(getActivity(), SharedPrefUtils.PREF_LOGIN_EMAIL, ""));
            mEmailView.setSelection(mEmailView.getText().length());
        }
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.btn_login).setOnClickListener(this);
        view.findViewById(R.id.btn_register).setOnClickListener(this);
        view.findViewById(R.id.btn_forgot_password).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String useremail = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                if (!useremail.isEmpty() && !password.isEmpty()) {
                    SharedPrefUtils.set(getActivity(), SharedPrefUtils.PREF_LOGIN_EMAIL, useremail);
                    mListener.onUserCredentialsCaptured(useremail, password);
                } else {
                    showLoginError(getString(R.string.error_invalid_credentials));
                }
                break;
            case R.id.btn_register:
                mListener.onNewRegistration();
                break;
            case R.id.btn_forgot_password:
                mListener.onForgotPasssword();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showLoginError(String msg) {
        mPasswordContainer.setError(msg);
    }

    public interface OnFragmentInteractionListener {
        void onUserCredentialsCaptured(String email, String password);
        void onNewRegistration();
        void onForgotPasssword();
    }
}
