package com.example.cinema.fragment;

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
import com.example.cinema.activity.AddFoodActivity;
import com.example.cinema.activity.MainActivity;
import com.example.cinema.adapter.FoodAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentFoodBinding;
import com.example.cinema.model.Food;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {

    private FragmentFoodBinding mFragmentFoodBinding;
    private List<Food> mListFood;
    private FoodAdapter mFoodAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentFoodBinding = FragmentFoodBinding.inflate(inflater, container, false);
        initView();
        getListFoods();
        return mFragmentFoodBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentFoodBinding.rcvFood.setLayoutManager(linearLayoutManager);

        mListFood = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mListFood, new FoodAdapter.IManagerFoodListener() {
            @Override
            public void editFood(Food food) {
                onClickEditFood(food);
            }

            @Override
            public void deleteFood(Food food) {
                deleteFoodItem(food);
            }
        });
        mFragmentFoodBinding.rcvFood.setAdapter(mFoodAdapter);

        mFragmentFoodBinding.btnAddFood.setOnClickListener(v -> onClickAddFood());
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
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showProgressDialog(true);
        MyApplication.get(getActivity()).getFoodDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        mainActivity.showProgressDialog(false);
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mFoodAdapter == null) {
                            return;
                        }
                        mListFood.add(0, food);
                        mFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mListFood.isEmpty() || mFoodAdapter == null) {
                            return;
                        }
                        for (Food foodEntity : mListFood) {
                            if (food.getId() == foodEntity.getId()) {
                                foodEntity.setName(food.getName());
                                foodEntity.setPrice(food.getPrice());
                                break;
                            }
                        }
                        mFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mListFood.isEmpty() || mFoodAdapter == null) {
                            return;
                        }
                        for (Food foodObject : mListFood) {
                            if (food.getId() == foodObject.getId()) {
                                mListFood.remove(foodObject);
                                break;
                            }
                        }
                        mFoodAdapter.notifyDataSetChanged();
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
