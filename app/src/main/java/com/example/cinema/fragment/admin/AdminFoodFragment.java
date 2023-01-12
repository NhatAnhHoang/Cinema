package com.example.cinema.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cinema.MyApplication;
import com.example.cinema.R;
import com.example.cinema.activity.admin.AddFoodActivity;
import com.example.cinema.adapter.admin.AdminFoodAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentAdminFoodBinding;
import com.example.cinema.model.Food;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodFragment extends Fragment {

    private FragmentAdminFoodBinding mFragmentAdminFoodBinding;
    private List<Food> mListFood;
    private AdminFoodAdapter mAdminFoodAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminFoodBinding = FragmentAdminFoodBinding.inflate(inflater, container, false);
        initView();
        getListFoods();
        return mFragmentAdminFoodBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminFoodBinding.rcvFood.setLayoutManager(linearLayoutManager);

        mListFood = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(mListFood, new AdminFoodAdapter.IManagerFoodListener() {
            @Override
            public void editFood(Food food) {
                onClickEditFood(food);
            }

            @Override
            public void deleteFood(Food food) {
                deleteFoodItem(food);
            }
        });
        mFragmentAdminFoodBinding.rcvFood.setAdapter(mAdminFoodAdapter);

        mFragmentAdminFoodBinding.btnAddFood.setOnClickListener(v -> onClickAddFood());
    }

    private void onClickAddFood() {
        GlobalFuntion.startActivity(getActivity(), AddFoodActivity.class);
    }

    private void onClickEditFood(Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFuntion.startActivity(getActivity(), AddFoodActivity.class, bundle);
    }

    private void deleteFoodItem(Food food) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getFoodDatabaseReference()
                            .child(String.valueOf(food.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.msg_delete_food_successfully), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public void getListFoods() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getFoodDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mAdminFoodAdapter == null) {
                            return;
                        }
                        mListFood.add(0, food);
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mListFood.isEmpty() || mAdminFoodAdapter == null) {
                            return;
                        }
                        for (Food foodEntity : mListFood) {
                            if (food.getId() == foodEntity.getId()) {
                                foodEntity.setName(food.getName());
                                foodEntity.setPrice(food.getPrice());
                                break;
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mListFood.isEmpty() || mAdminFoodAdapter == null) {
                            return;
                        }
                        for (Food foodObject : mListFood) {
                            if (food.getId() == foodObject.getId()) {
                                mListFood.remove(foodObject);
                                break;
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
