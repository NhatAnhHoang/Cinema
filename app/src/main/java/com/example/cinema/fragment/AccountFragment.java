package com.example.cinema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinema.activity.ChangePasswordActivity;
import com.example.cinema.activity.SignInActivity;
import com.example.cinema.constant.GlobalFunction;
import com.example.cinema.databinding.FragmentAccountBinding;
import com.example.cinema.prefs.DataStoreManager;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding mFragmentAccountBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false);

        mFragmentAccountBinding.tvEmail.setText(DataStoreManager.getUser().getEmail());

        mFragmentAccountBinding.layoutSignOut.setOnClickListener(v -> onClickSignOut());
        mFragmentAccountBinding.layoutChangePassword.setOnClickListener(v -> onClickChangePassword());

        return mFragmentAccountBinding.getRoot();
    }

    private void onClickChangePassword() {
        GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class);
    }

    private void onClickSignOut() {
        if (getActivity() == null) {
            return;
        }
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), SignInActivity.class);
        getActivity().finishAffinity();
    }
}
