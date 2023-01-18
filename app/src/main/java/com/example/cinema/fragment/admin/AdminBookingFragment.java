package com.example.cinema.fragment.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cinema.MyApplication;
import com.example.cinema.adapter.BookingHistoryAdapter;
import com.example.cinema.constant.GlobalFunction;
import com.example.cinema.databinding.FragmentAdminBookingBinding;
import com.example.cinema.model.BookingHistory;
import com.example.cinema.util.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingFragment extends Fragment {

    private FragmentAdminBookingBinding mFragmentAdminBookingBinding;
    private List<BookingHistory> mListBookingHistory;
    private BookingHistoryAdapter mBookingHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminBookingBinding = FragmentAdminBookingBinding.inflate(inflater, container, false);

        initListener();
        getListBookingHistory("");

        return mFragmentAdminBookingBinding.getRoot();
    }

    private void initListener() {
        mFragmentAdminBookingBinding.imgSearch.setOnClickListener(view1 -> searchBooking());

        mFragmentAdminBookingBinding.edtSearchId.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBooking();
                return true;
            }
            return false;
        });

        mFragmentAdminBookingBinding.edtSearchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    getListBookingHistory("");
                }
            }
        });
    }

    private void searchBooking() {
        String strKey = mFragmentAdminBookingBinding.edtSearchId.getText().toString().trim();
        getListBookingHistory(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    public void getListBookingHistory(String id) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getBookingDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListBookingHistory != null) {
                    mListBookingHistory.clear();
                } else {
                    mListBookingHistory = new ArrayList<>();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookingHistory bookingHistory = dataSnapshot.getValue(BookingHistory.class);
                    if (bookingHistory != null) {
                        if (StringUtil.isEmpty(id)) {
                            mListBookingHistory.add(0, bookingHistory);
                        } else {
                            if (String.valueOf(bookingHistory.getId()).contains(id)) {
                                mListBookingHistory.add(0, bookingHistory);
                            }
                        }
                    }
                }
                displayListBookingHistory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void displayListBookingHistory() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminBookingBinding.rcvBookingHistory.setLayoutManager(linearLayoutManager);

        mBookingHistoryAdapter = new BookingHistoryAdapter(getActivity(), true,
                mListBookingHistory, null);
        mFragmentAdminBookingBinding.rcvBookingHistory.setAdapter(mBookingHistoryAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBookingHistoryAdapter != null) mBookingHistoryAdapter.release();
    }
}
