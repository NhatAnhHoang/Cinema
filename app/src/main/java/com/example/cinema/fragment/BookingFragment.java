package com.example.cinema.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cinema.MyApplication;
import com.example.cinema.adapter.BookingHistoryAdapter;
import com.example.cinema.databinding.FragmentBookingBinding;
import com.example.cinema.model.BookingHistory;
import com.example.cinema.prefs.DataStoreManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {

    private FragmentBookingBinding mFragmentBookingBinding;
    private List<BookingHistory> mListBookingHistory;
    private BookingHistoryAdapter mBookingHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentBookingBinding = FragmentBookingBinding.inflate(inflater, container, false);
        initView();
        getListBookingHistory();
        return mFragmentBookingBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentBookingBinding.rcvBookingHistory.setLayoutManager(linearLayoutManager);

        mListBookingHistory = new ArrayList<>();
        mBookingHistoryAdapter = new BookingHistoryAdapter(mListBookingHistory);
        mFragmentBookingBinding.rcvBookingHistory.setAdapter(mBookingHistoryAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getListBookingHistory() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getBookingDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookingHistory bookingHistory = dataSnapshot.getValue(BookingHistory.class);
                    if (bookingHistory != null
                            && DataStoreManager.getUser().getEmail().equals(bookingHistory.getUser())) {
                        mListBookingHistory.add(0, bookingHistory);
                        mBookingHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
