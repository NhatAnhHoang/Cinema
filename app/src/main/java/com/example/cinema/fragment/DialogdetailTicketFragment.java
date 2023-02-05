package com.example.cinema.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cinema.R;
import com.example.cinema.databinding.ItemDetailBookingHistoryBinding;

public class DialogdetailTicketFragment extends DialogFragment {
    private ItemDetailBookingHistoryBinding itemDetailBookingHistoryBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         return inflater.inflate(R.layout.item_detail_booking_history,container,false);
    }
}
