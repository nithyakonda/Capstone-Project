package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.Builder builder = new User.Builder(
                        mEmailView.getText().toString()
                );
                // TODO: 6/20/18 enable button only if passwords match
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
}
