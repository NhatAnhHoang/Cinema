package com.example.cinema.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinema.databinding.FragmentAdminCategoryBinding;

public class AdminCategoryFragment extends Fragment {

    private FragmentAdminCategoryBinding mFragmentAdminCategoryBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminCategoryBinding = FragmentAdminCategoryBinding.inflate(inflater, container, false);
        return mFragmentAdminCategoryBinding.getRoot();
    }
}
