package com.example.cinema.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cinema.MyApplication;
import com.example.cinema.R;
import com.example.cinema.activity.admin.AddCategoryActivity;
import com.example.cinema.adapter.admin.AdminCategoryAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentAdminCategoryBinding;
import com.example.cinema.model.Category;
import com.example.cinema.util.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryFragment extends Fragment {

    private FragmentAdminCategoryBinding mFragmentAdminCategoryBinding;
    private List<Category> mListCategory;
    private AdminCategoryAdapter mAdminCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminCategoryBinding = FragmentAdminCategoryBinding.inflate(inflater, container, false);

        initView();
        initListener();
        getListCategory("");
        return mFragmentAdminCategoryBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminCategoryBinding.rcvCategory.setLayoutManager(linearLayoutManager);

        mListCategory = new ArrayList<>();
        mAdminCategoryAdapter = new AdminCategoryAdapter(mListCategory,
                new AdminCategoryAdapter.IManagerCategoryListener() {
            @Override
            public void editCategory(Category category) {
                onClickEditCategory(category);
            }

            @Override
            public void deleteCategory(Category category) {
                deleteCategoryItem(category);
            }
        });
        mFragmentAdminCategoryBinding.rcvCategory.setAdapter(mAdminCategoryAdapter);
    }

    private void initListener() {
        mFragmentAdminCategoryBinding.btnAddCategory.setOnClickListener(v -> onClickAddCategory());

        mFragmentAdminCategoryBinding.imgSearch.setOnClickListener(view1 -> searchCategory());

        mFragmentAdminCategoryBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCategory();
                return true;
            }
            return false;
        });

        mFragmentAdminCategoryBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    getListCategory("");
                }
            }
        });
    }

    private void searchCategory() {
        String strKey = mFragmentAdminCategoryBinding.edtSearchName.getText().toString().trim();
        getListCategory(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    private void onClickAddCategory() {
        GlobalFuntion.startActivity(getActivity(), AddCategoryActivity.class);
    }

    private void onClickEditCategory(Category category) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.KEY_INTENT_CATEGORY_OBJECT, category);
        GlobalFuntion.startActivity(getActivity(), AddCategoryActivity.class, bundle);
    }

    private void deleteCategoryItem(Category category) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getCategoryDatabaseReference()
                            .child(String.valueOf(category.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.msg_delete_category_successfully), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public void getListCategory(String key) {
        if (getActivity() == null) {
            return;
        }
        if (mListCategory != null) {
            mListCategory.clear();
        } else {
            mListCategory = new ArrayList<>();
        }
        MyApplication.get(getActivity()).getCategoryDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Category category = dataSnapshot.getValue(Category.class);
                        if (category == null || mListCategory == null || mAdminCategoryAdapter == null) {
                            return;
                        }
                        if (StringUtil.isEmpty(key)) {
                            mListCategory.add(0, category);
                        } else {
                            if (GlobalFuntion.getTextSearch(category.getName()).toLowerCase().trim()
                                    .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                                mListCategory.add(0, category);
                            }
                        }
                        mAdminCategoryAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Category category = dataSnapshot.getValue(Category.class);
                        if (category == null || mListCategory == null
                                || mListCategory.isEmpty() || mAdminCategoryAdapter == null) {
                            return;
                        }
                        for (int i = 0; i < mListCategory.size(); i++) {
                            Category categoryEntity = mListCategory.get(i);
                            if (category.getId() == categoryEntity.getId()) {
                                mListCategory.set(i, category);
                                break;
                            }
                        }
                        mAdminCategoryAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Category category = dataSnapshot.getValue(Category.class);
                        if (category == null || mListCategory == null
                                || mListCategory.isEmpty() || mAdminCategoryAdapter == null) {
                            return;
                        }
                        for (Category categoryObject : mListCategory) {
                            if (category.getId() == categoryObject.getId()) {
                                mListCategory.remove(categoryObject);
                                break;
                            }
                        }
                        mAdminCategoryAdapter.notifyDataSetChanged();
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
