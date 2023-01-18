package com.example.cinema.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema.R;
import com.example.cinema.databinding.ItemBookingHistoryBinding;
import com.example.cinema.listener.IOnSingleClickListener;
import com.example.cinema.model.BookingHistory;
import com.example.cinema.util.DateTimeUtils;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder> {

    private Context mContext;
    private final List<BookingHistory> mListBookingHistory;
    private final IClickQRListener iClickQRListener;
    private final boolean mIsAdmin;

    public interface IClickQRListener {
        void onClickOpenQrCode(String id);
    }

    public BookingHistoryAdapter(Context context, boolean isAdmin,
                                 List<BookingHistory> mListBookingHistory, IClickQRListener listener) {
        this.mContext = context;
        this.mIsAdmin = isAdmin;
        this.mListBookingHistory = mListBookingHistory;
        this.iClickQRListener = listener;
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
        if (DateTimeUtils.convertDateToTimeStamp(bookingHistory.getDate()) < DateTimeUtils.getLongCurrentTimeStamp()) {
            holder.mItemBookingHistoryBinding.layoutItem.setBackgroundColor(mContext.getResources().getColor(R.color.black_overlay));
        } else {
            holder.mItemBookingHistoryBinding.layoutItem.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        holder.mItemBookingHistoryBinding.tvId.setText(String.valueOf(bookingHistory.getId()));
        holder.mItemBookingHistoryBinding.tvNameMovie.setText(bookingHistory.getName());
        holder.mItemBookingHistoryBinding.tvDateMovie.setText(bookingHistory.getDate());
        holder.mItemBookingHistoryBinding.tvRoomMovie.setText(bookingHistory.getRoom());
        holder.mItemBookingHistoryBinding.tvTimeMovie.setText(bookingHistory.getTime());
        holder.mItemBookingHistoryBinding.tvCountBooking.setText(bookingHistory.getCount());
        holder.mItemBookingHistoryBinding.tvCountSeat.setText(bookingHistory.getSeats());
        holder.mItemBookingHistoryBinding.tvFoodDrink.setText(bookingHistory.getFoods());
        holder.mItemBookingHistoryBinding.tvPaymentMethod.setText(bookingHistory.getPayment());
        holder.mItemBookingHistoryBinding.tvTotalAmount.setText(bookingHistory.getTotal());

        if (mIsAdmin) {
            holder.mItemBookingHistoryBinding.imgQr.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.layoutEmail.setVisibility(View.VISIBLE);
            holder.mItemBookingHistoryBinding.tvEmail.setText(bookingHistory.getUser());
        } else {
            holder.mItemBookingHistoryBinding.layoutEmail.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.imgQr.setVisibility(View.VISIBLE);
            holder.mItemBookingHistoryBinding.imgQr.setOnClickListener(new IOnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    iClickQRListener.onClickOpenQrCode(String.valueOf(bookingHistory.getId()));
                }
            });
        }
    }

    public void release() {
        mContext = null;
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
