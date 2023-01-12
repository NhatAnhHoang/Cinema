package com.example.cinema.fragment.admin;

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
import com.example.cinema.databinding.FragmentAdminManageBinding;
import com.example.cinema.prefs.DataStoreManager;
import com.google.firebase.auth.FirebaseAuth;

public class AdminManageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAdminManageBinding fragmentAdminManageBinding = FragmentAdminManageBinding.inflate(inflater, container, false);

        fragmentAdminManageBinding.tvEmail.setText(DataStoreManager.getUser().getEmail());

        fragmentAdminManageBinding.layoutSignOut.setOnClickListener(v -> onClickSignOut());
        fragmentAdminManageBinding.layoutChangePassword.setOnClickListener(v -> onClickChangePassword());

        return fragmentAdminManageBinding.getRoot();
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
