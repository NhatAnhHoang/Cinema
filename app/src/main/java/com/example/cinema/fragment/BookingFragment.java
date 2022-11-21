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
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentBookingBinding;
import com.example.cinema.model.BookingHistory;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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

    public void getListBookingHistory() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getBookingDatabaseReference().child(GlobalFuntion.getStringEmailUser())
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        BookingHistory bookingHistory = dataSnapshot.getValue(BookingHistory.class);
                        if (bookingHistory == null || mListBookingHistory == null
                                || mBookingHistoryAdapter == null) {
                            return;
                        }
                        mListBookingHistory.add(0, bookingHistory);
                        mBookingHistoryAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        BookingHistory bookingHistory = dataSnapshot.getValue(BookingHistory.class);
                        if (bookingHistory == null || mListBookingHistory == null
                                || mListBookingHistory.isEmpty() || mBookingHistoryAdapter == null) {
                            return;
                        }
                        for (int i = 0; i < mListBookingHistory.size(); i++) {
                            if (bookingHistory.getId() == mListBookingHistory.get(i).getId()) {
                                mListBookingHistory.set(i, bookingHistory);
                                break;
                            }
                        }
                        mBookingHistoryAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        BookingHistory bookingHistory = dataSnapshot.getValue(BookingHistory.class);
                        if (bookingHistory == null || mListBookingHistory == null
                                || mListBookingHistory.isEmpty() || mBookingHistoryAdapter == null) {
                            return;
                        }
                        for (BookingHistory bookingObject : mListBookingHistory) {
                            if (bookingHistory.getId() == bookingObject.getId()) {
                                mListBookingHistory.remove(bookingObject);
                                break;
                            }
                        }
                        mBookingHistoryAdapter.notifyDataSetChanged();
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
