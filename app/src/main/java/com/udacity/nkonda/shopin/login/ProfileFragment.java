package com.udacity.nkonda.shopin.login;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.abdularis.civ.AvatarImageView;
import com.udacity.nkonda.shopin.R;
import com.udacity.nkonda.shopin.data.User;
import com.udacity.nkonda.shopin.util.UiUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_IS_FIRST_TIME_LOGIN = "ARG_IS_FIRST_TIME_LOGIN";
    private static final String ARG_USER = "ARG_USER";
    private static final int PICK_AVATAR = 100;

    private boolean mFirstTimeLogin;
    private User mUser;
    private Uri mPhotoUri;

    private OnFragmentInteractionListener mListener;

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
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mFirstTimeLogin = getArguments().getBoolean(ARG_IS_FIRST_TIME_LOGIN);
                mUser = getArguments().getParcelable(ARG_USER);
            }
        } else {
            mFirstTimeLogin = savedInstanceState.getBoolean(ARG_IS_FIRST_TIME_LOGIN);
            mUser = savedInstanceState.getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        mAddPhotoBtn.setOnClickListener(this);
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
        initAvatar();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_IS_FIRST_TIME_LOGIN, mFirstTimeLogin);
        outState.putParcelable(ARG_USER, mUser);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add_photo) {
            Intent intent;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }else{
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_AVATAR);
        } else if (v.getId() == R.id.btn_update_profile) {
            mListener.onProfileInfoCaptured(mDisplayNameView.getText().toString(), mUser.getDisplayPicture());
        } else if (v.getId() == R.id.btn_logout) {
            mListener.onLogout();
        } else if (v.getId() == R.id.et_display_name) {
            mDisplayNameView.setCursorVisible(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICK_AVATAR && resultCode == Activity.RESULT_OK) {
            mPhotoUri = intent.getData();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final int takeFlags = intent.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                ContentResolver resolver = getActivity().getContentResolver();
                resolver.takePersistableUriPermission(mPhotoUri, takeFlags);
                mUser.setDisplayPicture(mPhotoUri);
            }
            showAvatarImage(UiUtils.getAvatarBitmap(getActivity(), mPhotoUri));
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
        mLogoutBtn.setVisibility(View.VISIBLE);
    }

    private void showAvatarImage(Bitmap bitmap) {
        mAvatarImageView.setState(AvatarImageView.SHOW_IMAGE);
        mAvatarImageView.setStrokeWidth(0);
        mAvatarImageView.setImageBitmap(bitmap);
    }

    private void showAvatarInitial(String displayName) {
        mAvatarImageView.setState(AvatarImageView.SHOW_INITIAL);
        mAvatarImageView.setStrokeWidth(2);
        mAvatarImageView.setText(displayName);
    }

    private void initAvatar() {
        if (mUser.getDisplayPicture() != null) {
            Bitmap bitmap = UiUtils.getAvatarBitmap(getActivity(), mUser.getDisplayPicture());
            if (bitmap != null) {
                showAvatarImage(bitmap);
                return;
            }
        }
        showAvatarInitial(mDisplayNameView.getText().toString().toUpperCase());
    }
}
