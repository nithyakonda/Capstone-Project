package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.container_email)
    TextInputLayout mEmailContainer;

    @BindView(R.id.et_email)
    EditText mEmailView;

    @BindView(R.id.container_password)
    TextInputLayout mPwdContainer;

    @BindView(R.id.et_password)
    EditText mPasswordView;

    @BindView(R.id.container_confirm_password)
    TextInputLayout mConfirmPwdContainer;

    @BindView(R.id.et_confirm_password)
    EditText mConfirmPasswordView;

    @BindView(R.id.btn_create)
    Button mCreateBtn;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        mEmailView.requestFocus();
        mConfirmPasswordView.addTextChangedListener(new EmailTextWatcher());
        mConfirmPasswordView.setTextColor(getResources().getColor(R.color.colorInvalidText));
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.Builder builder = new User.Builder(
                        mEmailView.getText().toString()
                );
                mListener.onNewUserInfoCaptured(builder.createUser(), mPasswordView.getText().toString());
            }
        });

        return view;
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

    public void showPasswordError(String msg) {
        mPwdContainer.setError(msg);
    }

    public void showEmailError(String msg) {
        mEmailContainer.setError(msg);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onNewUserInfoCaptured(User user, String password);
    }

    private class EmailTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mConfirmPasswordView.getText().toString()
                    .equals(mPasswordView.getText().toString())) {
                mCreateBtn.setEnabled(true);
                mConfirmPasswordView.setTextColor(getResources().getColor(R.color.colorValidText));
            } else {
                mCreateBtn.setEnabled(false);
            }
        }
    }
}
