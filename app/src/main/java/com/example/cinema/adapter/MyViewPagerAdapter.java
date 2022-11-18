package com.example.cinema.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cinema.fragment.admin.AdminHomeFragment;
import com.example.cinema.fragment.BookingFragment;
import com.example.cinema.fragment.admin.FoodFragment;
import com.example.cinema.fragment.HomeFragment;
import com.example.cinema.fragment.AccountFragment;
import com.example.cinema.prefs.DataStoreManager;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (DataStoreManager.getUser().isAdmin()) {
                    return new AdminHomeFragment();
                }
                return new HomeFragment();

            case 1:
                if (DataStoreManager.getUser().isAdmin()) {
                    return new FoodFragment();
                }
                return new BookingFragment();

            case 2:
                return new AccountFragment();

            default:
                if (DataStoreManager.getUser().isAdmin()) {
                    return new AdminHomeFragment();
                }
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
