package com.example.cinema.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.cinema.MyApplication;
import com.example.cinema.R;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.databinding.ItemBookingHistoryBinding;
import com.example.cinema.databinding.ItemDetailBookingHistoryBinding;
import com.example.cinema.fragment.DialogdetailTicketFragment;
import com.example.cinema.listener.IOnSingleClickListener;
import com.example.cinema.model.BookingHistory;
import com.example.cinema.model.Food;
import com.example.cinema.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder> {

    private Context mContext;
    private final List<BookingHistory> mListBookingHistory;
    private final IClickQRListener iClickQRListener;
    private final IClickConfirmListener iClickConfirmListener;
    private final BookingListener bookingListener;
    private final boolean mIsAdmin;
    private Dialog mDialog;

    public interface IClickQRListener {
        void onClickOpenQrCode(String id);
    }

    public interface IClickConfirmListener {
        void onClickConfirmBooking(String id);
    }

    public BookingHistoryAdapter(Context context, boolean isAdmin,
                                 List<BookingHistory> mListBookingHistory, IClickQRListener listener, IClickConfirmListener confirmListener) {
        this.mContext = context;
        this.mIsAdmin = isAdmin;
        this.mListBookingHistory = mListBookingHistory;
        this.iClickQRListener = listener;
        this.iClickConfirmListener = confirmListener;
        bookingListener = null;
    }

    public BookingHistoryAdapter(Context context, boolean isAdmin,
                                 List<BookingHistory> mListBookingHistory, IClickQRListener listener, IClickConfirmListener confirmListener, BookingListener bookingListener) {
        this.mContext = context;
        this.mIsAdmin = isAdmin;
        this.mListBookingHistory = mListBookingHistory;
        this.iClickQRListener = listener;
        this.iClickConfirmListener = confirmListener;
        this.bookingListener = bookingListener;
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
        boolean isExpire = DateTimeUtils.convertDateToTimeStamp(bookingHistory.getDate()) < DateTimeUtils.getLongCurrentTimeStamp();
        if (isExpire || bookingHistory.isUsed()) {
            holder.mItemBookingHistoryBinding.LayoutIMG.setVisibility(View.VISIBLE);

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
        String strTotal = bookingHistory.getTotal() + ConstantKey.UNIT_CURRENCY;
        holder.mItemBookingHistoryBinding.tvTotalAmount.setText(strTotal);
        holder.mItemBookingHistoryBinding.tvDateCreate.setText(DateTimeUtils.convertTimeStampToDate(String.valueOf(bookingHistory.getId())));
        holder.mItemBookingHistoryBinding.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bookingHistory.isUsed()) {

                    mDialog = new Dialog(v.getContext());
                    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mDialog.setContentView(R.layout.item_detail_booking_history);
                    Window window = mDialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    mDialog.setCancelable(true);

                    // Get view
                    final TextView tvID = mDialog.findViewById(R.id.tv_id);
                    final TextView tvNameMovie = mDialog.findViewById(R.id.tv_name_movie);
                    final TextView tvDateMovie = mDialog.findViewById(R.id.tv_date_movie);
                    final TextView tvRoomMovie = mDialog.findViewById(R.id.tv_room_movie);
                    final TextView tvTimeMovie = mDialog.findViewById(R.id.tv_time_movie);
                    final TextView tvCountBooking = mDialog.findViewById(R.id.tv_count_booking);
                    final TextView tvCountSeat = mDialog.findViewById(R.id.tv_count_seat);
                    final TextView tvFoodDrink = mDialog.findViewById(R.id.tv_food_drink);
                    final TextView tvPaymentMethod = mDialog.findViewById(R.id.tv_payment_method);
                    final TextView tvTotaDate = mDialog.findViewById(R.id.tv_date_create);
                    final TextView tvTotal = mDialog.findViewById(R.id.tv_total_amount);


                    // Set data
                    tvID.setText(String.valueOf(bookingHistory.getId()));
                    tvNameMovie.setText(bookingHistory.getName());
                    tvDateMovie.setText(bookingHistory.getDate());
                    tvRoomMovie.setText(bookingHistory.getRoom());
                    tvTimeMovie.setText(bookingHistory.getTime());
                    tvCountBooking.setText(bookingHistory.getCount());
                    tvCountSeat.setText(bookingHistory.getSeats());
                    tvFoodDrink.setText(bookingHistory.getFoods());
                    tvPaymentMethod.setText(bookingHistory.getPayment());
                    tvTotaDate.setText(DateTimeUtils.convertTimeStampToDate(String.valueOf(bookingHistory.getId())));
                    tvFoodDrink.setText(bookingHistory.getFoods());
                    tvTotal.setText(strTotal);
                    mDialog.show();

                }else {
                    Toast.makeText(v.getContext(), "Vé đã sử dụng", Toast.LENGTH_SHORT).show();
                }


            }
//                bookingListener.onclickTicket(bookingHistory);
        });
        if (mIsAdmin) {
            holder.mItemBookingHistoryBinding.imgQr.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.layoutEmail.setVisibility(View.VISIBLE);
            holder.mItemBookingHistoryBinding.tvEmail.setText(bookingHistory.getUser());
            if (isExpire || bookingHistory.isUsed()) {
                holder.mItemBookingHistoryBinding.layoutConfirm.setVisibility(View.GONE);
            } else {
                holder.mItemBookingHistoryBinding.layoutConfirm.setVisibility(View.VISIBLE);
                holder.mItemBookingHistoryBinding.chbConfirm.setOnClickListener(new IOnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        new MaterialDialog.Builder(v.getContext())
                                .title(bookingHistory.getName())
                                .content(
                                        bookingHistory.getRoom() +
                                                "\nGhế "
                                                + bookingHistory.getSeats()
                                )
                                .positiveText("oke")
                                .negativeText("không")
                                .onPositive((dialog, which) -> {
                                    iClickConfirmListener.onClickConfirmBooking(String.valueOf(bookingHistory.getId()));
                                    dialog.dismiss();
                                })
                                .onNegative((dialog, which) -> {
                                    dialog.dismiss();
                                    holder.mItemBookingHistoryBinding.chbConfirm.setChecked(false);
                                })
                                .show();
                    }
                });
            }
        } else {
            holder.mItemBookingHistoryBinding.labelTotalAmount.setText(R.string.label_id);
            holder.mItemBookingHistoryBinding.tvTotalAmount.setText(String.valueOf(bookingHistory.getId()));
            holder.mItemBookingHistoryBinding.layoutRoom.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.layoutID.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.LayoutCountSeat.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.LayoutFood.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.LayoutSeat.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.LayoutDate.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.LayoutPayment.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.layoutConfirm.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.layoutEmail.setVisibility(View.GONE);
            holder.mItemBookingHistoryBinding.imgQr.setVisibility(View.VISIBLE);
            if (isExpire || bookingHistory.isUsed()) {
                holder.mItemBookingHistoryBinding.imgQr.setOnClickListener(null);
            } else {
                holder.mItemBookingHistoryBinding.imgQr.setOnClickListener(new IOnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        iClickQRListener.onClickOpenQrCode(String.valueOf(bookingHistory.getId()));
                    }
                });
            }
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

    public interface BookingListener {
        void onclickTicket(BookingHistory item);
    }

}
