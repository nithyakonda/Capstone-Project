package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.udacity.nkonda.shopin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.et_email)
    EditText mEmailView;

    @BindView(R.id.btn_send_pwd)
    Button mSendPasswordBtn;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.bind(this, view);

        mEmailView.requestFocus();
        mSendPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 6/20/18 enable button only if text entered is a valid email
                mListener.onPasswordResetEmailCaptured(mEmailView.getText().toString());
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onPasswordResetEmailCaptured(String email);
    }
}
