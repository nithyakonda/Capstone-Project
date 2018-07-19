package com.udacity.nkonda.shopin.login;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.abdularis.civ.AvatarImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_IS_FIRST_TIME_LOGIN = "ARG_IS_FIRST_TIME_LOGIN";
    private static final String ARG_USER = "ARG_USER";

    private boolean mFirstTimeLogin;
    private User mUser;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.tv_title)
    TextView mTitleView;

    @BindView(R.id.iv_avatar)
    AvatarImageView mAvatarImageView;

    @BindView(R.id.fab_add_photo)
    FloatingActionButton mAddPhotoBtn;

    @BindView(R.id.et_display_name)
    EditText mDisplayNameView;

    @BindView(R.id.et_email)
    EditText mEmailView;

    @BindView(R.id.btn_update_profile)
    Button mUpdateBtn;

    @BindView(R.id.btn_logout)
    Button mLogoutBtn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(boolean isFirstTimeLogin, User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_FIRST_TIME_LOGIN, isFirstTimeLogin);
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFirstTimeLogin = getArguments().getBoolean(ARG_IS_FIRST_TIME_LOGIN);
            mUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        mUpdateBtn.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);
        mDisplayNameView.setOnClickListener(this);

        if (mFirstTimeLogin) {
            showWelcomeUi();
        } else {
            showProfileUi();
        }

        if (mUser.getDisplayName() == null || mUser.getDisplayName().isEmpty()) {
            mDisplayNameView.setText(mUser.getEmail());
        } else {
            mDisplayNameView.setText(mUser.getDisplayName());
        }
        mDisplayNameView.setCursorVisible(false);

        mEmailView.setText(mUser.getEmail());
        mAvatarImageView.setText(mDisplayNameView.getText().toString().toUpperCase());
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_profile) {
            // TODO: 7/17/18 capture photourl
            mListener.onProfileInfoCaptured(mDisplayNameView.getText().toString(), null);
        } else if (v.getId() == R.id.btn_logout) {
            mListener.onLogout();
        } else if (v.getId() == R.id.et_display_name) {
            mDisplayNameView.setCursorVisible(true);
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


    public interface OnFragmentInteractionListener {
        void onProfileInfoCaptured(String displayName, Uri photoUrl);
        void onLogout();
    }

    private void showWelcomeUi() {
        mLogoutBtn.setVisibility(View.GONE);
    }

    private void showProfileUi() {
        mTitleView.setVisibility(View.GONE);
        mLogoutBtn.setVisibility(View.VISIBLE);
    }
}