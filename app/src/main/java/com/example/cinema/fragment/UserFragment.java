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
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentUserBinding;
import com.example.cinema.prefs.DataStoreManager;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {

    private FragmentUserBinding mFragmentUserBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentUserBinding = FragmentUserBinding.inflate(inflater, container, false);

        mFragmentUserBinding.tvEmail.setText(DataStoreManager.getUser().getEmail());

        mFragmentUserBinding.layoutSignOut.setOnClickListener(v -> onClickSignOut());
        mFragmentUserBinding.layoutChangePassword.setOnClickListener(v -> onClickChangePassword());

        return mFragmentUserBinding.getRoot();
    }

    private void onClickChangePassword() {
        GlobalFuntion.startActivity(getActivity(), ChangePasswordActivity.class);
    }

    private void onClickSignOut() {
        if (getActivity() == null) {
            return;
        }
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFuntion.startActivity(getActivity(), SignInActivity.class);
        getActivity().finishAffinity();
    }
}
