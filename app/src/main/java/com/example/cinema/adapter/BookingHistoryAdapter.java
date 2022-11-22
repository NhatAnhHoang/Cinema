package com.example.cinema.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema.databinding.ItemBookingHistoryBinding;
import com.example.cinema.model.BookingHistory;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder> {

    private final List<BookingHistory> mListBookingHistory;

    public BookingHistoryAdapter(List<BookingHistory> mListBookingHistory) {
        this.mListBookingHistory = mListBookingHistory;
    }

    @NonNull
    @Override
    public BookingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingHistoryBinding itemBookingHistoryBinding = ItemBookingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingHistoryViewHolder(itemBookingHistoryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryViewHolder holder, int position) {
        BookingHistory bookingHistory = mListBookingHistory.get(position);
        if (bookingHistory == null) {
            return;
        }
        holder.mItemBookingHistoryBinding.tvNameMovie.setText(bookingHistory.getName());
        holder.mItemBookingHistoryBinding.tvDateMovie.setText(bookingHistory.getDate());
        holder.mItemBookingHistoryBinding.tvRoomMovie.setText(bookingHistory.getRoom());
        holder.mItemBookingHistoryBinding.tvTimeMovie.setText(bookingHistory.getTime());
        holder.mItemBookingHistoryBinding.tvCountBooking.setText(bookingHistory.getCount());
        holder.mItemBookingHistoryBinding.tvCountSeat.setText(bookingHistory.getSeats());
        holder.mItemBookingHistoryBinding.tvFoodDrink.setText(bookingHistory.getFoods());
        holder.mItemBookingHistoryBinding.tvTotalAmount.setText(bookingHistory.getTotal());
    }

    @Override
    public int getItemCount() {
        if (mListBookingHistory != null) {
            return mListBookingHistory.size();
        }
        return 0;
    }

    public static class BookingHistoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemBookingHistoryBinding mItemBookingHistoryBinding;

        public BookingHistoryViewHolder(@NonNull ItemBookingHistoryBinding itemBookingHistoryBinding) {
            super(itemBookingHistoryBinding.getRoot());
            this.mItemBookingHistoryBinding = itemBookingHistoryBinding;
        }
    }
}
