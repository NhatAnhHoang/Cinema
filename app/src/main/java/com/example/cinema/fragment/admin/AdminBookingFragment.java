package com.example.cinema.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinema.databinding.FragmentAdminBookingBinding;

public class AdminBookingFragment extends Fragment {

    private FragmentAdminBookingBinding mFragmentAdminBookingBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminBookingBinding = FragmentAdminBookingBinding.inflate(inflater, container, false);
        return mFragmentAdminBookingBinding.getRoot();
    }
}
