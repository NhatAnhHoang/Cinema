package com.example.cinema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinema.databinding.FragmentHomeAdminBinding;

public class AdminHomeFragment extends Fragment {

    private FragmentHomeAdminBinding mFragmentHomeAdminBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeAdminBinding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        return mFragmentHomeAdminBinding.getRoot();
    }
}
