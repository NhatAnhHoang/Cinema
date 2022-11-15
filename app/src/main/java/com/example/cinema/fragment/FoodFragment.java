package com.example.cinema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cinema.databinding.FragmentFoodBinding;

public class FoodFragment extends Fragment {

    private FragmentFoodBinding mFragmentFoodBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentFoodBinding = FragmentFoodBinding.inflate(inflater, container, false);
        return mFragmentFoodBinding.getRoot();
    }
}
