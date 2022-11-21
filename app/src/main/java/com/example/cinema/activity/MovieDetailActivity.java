package com.example.cinema.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.cinema.MyApplication;
import com.example.cinema.R;
import com.example.cinema.adapter.FoodDrinkAdapter;
import com.example.cinema.adapter.RoomAdapter;
import com.example.cinema.adapter.TimeAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.ActivityMovieDetailBinding;
import com.example.cinema.listener.IOnSingleClickListener;
import com.example.cinema.model.BookingHistory;
import com.example.cinema.model.Food;
import com.example.cinema.model.Movie;
import com.example.cinema.model.Room;
import com.example.cinema.model.SlotTime;
import com.example.cinema.prefs.DataStoreManager;
import com.example.cinema.util.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding mActivityMovieDetailBinding;
    private Movie mMovie;

    private List<Room> mListRooms;
    private RoomAdapter mRoomAdapter;
    private String mTitleRoomSelected;

    private List<SlotTime> mListTimes;
    private TimeAdapter mTimeAdapter;
    private String mTitleTimeSelected;

    private List<Food> mListFood;
    private FoodDrinkAdapter mFoodDrinkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMovieDetailBinding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityMovieDetailBinding.getRoot());

        getDataIntent();
        displayDataMovie();

        mActivityMovieDetailBinding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mMovie = (Movie) bundle.get(ConstantKey.KEY_INTENT_MOVIE_OBJECT);
    }

    private void displayDataMovie() {
        if (mMovie == null) {
            return;
        }
        if (!StringUtil.isEmpty(mMovie.getImage())) {
            Glide.with(this).load(mMovie.getImage()).error(R.drawable.img_no_available)
                    .into(mActivityMovieDetailBinding.imgMovie);
        } else {
            mActivityMovieDetailBinding.imgMovie.setImageResource(R.drawable.img_no_available);
        }
        mActivityMovieDetailBinding.tvTitleMovie.setText(mMovie.getName());
        mActivityMovieDetailBinding.tvDateMovie.setText(mMovie.getDate());
        String strPrice = mMovie.getPrice() + ConstantKey.UNIT_CURRENCY_MOVIE;
        mActivityMovieDetailBinding.tvPriceMovie.setText(strPrice);
        mActivityMovieDetailBinding.tvDescriptionMovie.setText(mMovie.getDescription());

        if (!StringUtil.isEmpty(mMovie.getUrl())) {
            Log.e("Movie Url" , mMovie.getUrl());
        }

        showListRooms();
        showListTimes();
        initListFoodAndDrink();

        mActivityMovieDetailBinding.btnBooking.setOnClickListener(view -> onClickBookingMovie());
    }

    private void showListRooms() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mActivityMovieDetailBinding.rcvRoom.setLayoutManager(gridLayoutManager);

        mListRooms = GlobalFuntion.getListRooms();
        mRoomAdapter = new RoomAdapter(mListRooms, this::onClickSelectRoom);
        mActivityMovieDetailBinding.rcvRoom.setAdapter(mRoomAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onClickSelectRoom(Room room) {
        for (int i = 0; i < mListRooms.size(); i++) {
            mListRooms.get(i).setSelected(mListRooms.get(i).getId() == room.getId());
        }
        mRoomAdapter.notifyDataSetChanged();
    }

    private String getTitleRoomSelected() {
        for (Room room : mListRooms) {
            if (room.isSelected()) {
                mTitleRoomSelected = room.getTitle();
                break;
            }
        }
        return mTitleRoomSelected;
    }

    private void showListTimes() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mActivityMovieDetailBinding.rcvTime.setLayoutManager(gridLayoutManager);

        mListTimes = GlobalFuntion.getListSlotTimes();
        mTimeAdapter = new TimeAdapter(mListTimes, this::onClickSelectTime);
        mActivityMovieDetailBinding.rcvTime.setAdapter(mTimeAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onClickSelectTime(SlotTime time) {
        for (int i = 0; i < mListTimes.size(); i++) {
            mListTimes.get(i).setSelected(mListTimes.get(i).getId() == time.getId());
        }
        mTimeAdapter.notifyDataSetChanged();
    }

    private String getTitleTimeSelected() {
        for (SlotTime time : mListTimes) {
            if (time.isSelected()) {
                mTitleTimeSelected = time.getTitle();
                break;
            }
        }
        return mTitleTimeSelected;
    }

    private void initListFoodAndDrink() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mActivityMovieDetailBinding.rcvFoodDrink.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mActivityMovieDetailBinding.rcvFoodDrink.addItemDecoration(decoration);

        mListFood = new ArrayList<>();
        mFoodDrinkAdapter = new FoodDrinkAdapter(mListFood, this::selectedCountFoodAndDrink);
        mActivityMovieDetailBinding.rcvFoodDrink.setAdapter(mFoodDrinkAdapter);

        getListFoodAndDrink();
    }

    public void getListFoodAndDrink() {
        MyApplication.get(this).getFoodDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mFoodDrinkAdapter == null) {
                            return;
                        }
                        mListFood.add(0, food);
                        mFoodDrinkAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mListFood.isEmpty() || mFoodDrinkAdapter == null) {
                            return;
                        }
                        for (Food foodEntity : mListFood) {
                            if (food.getId() == foodEntity.getId()) {
                                foodEntity.setName(food.getName());
                                foodEntity.setPrice(food.getPrice());
                                foodEntity.setCount(food.getCount());
                                break;
                            }
                        }
                        mFoodDrinkAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mListFood.isEmpty() || mFoodDrinkAdapter == null) {
                            return;
                        }
                        for (Food foodObject : mListFood) {
                            if (food.getId() == foodObject.getId()) {
                                mListFood.remove(foodObject);
                                break;
                            }
                        }
                        mFoodDrinkAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void selectedCountFoodAndDrink(Food food, int count) {
        if (mListFood == null || mListFood.isEmpty()) {
            return;
        }
        for (Food foodEntity : mListFood) {
            if (foodEntity.getId() == food.getId()) {
                foodEntity.setCount(count);
                break;
            }
        }
    }

    private void onClickBookingMovie() {
        if (mMovie == null) {
            return;
        }
        String strCountBooking = mActivityMovieDetailBinding.edtCountBooking.getText().toString().trim();
        if (StringUtil.isEmpty(strCountBooking)) {
            Toast.makeText(this, getString(R.string.msg_booking_count_require), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!StringUtil.isQuantity(strCountBooking)) {
            Toast.makeText(this, getString(R.string.msg_booking_count_invalid), Toast.LENGTH_SHORT).show();
            return;
        }
        showDialogConfirmBooking();
    }

    private void showDialogConfirmBooking() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_confirm_booking);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvNameMovie = dialog.findViewById(R.id.tv_name_movie);
        final TextView tvDateMovie = dialog.findViewById(R.id.tv_date_movie);
        final TextView tvRoomMovie = dialog.findViewById(R.id.tv_room_movie);
        final TextView tvTimeMovie = dialog.findViewById(R.id.tv_time_movie);
        final TextView tvCountBooking = dialog.findViewById(R.id.tv_count_booking);
        final TextView tvFoodDrink = dialog.findViewById(R.id.tv_food_drink);
        final TextView tvTotalAmount = dialog.findViewById(R.id.tv_total_amount);

        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogOk = dialog.findViewById(R.id.tv_dialog_ok);

        // Set data
        tvNameMovie.setText(mMovie.getName());
        tvDateMovie.setText(mMovie.getDate());
        tvRoomMovie.setText(getTitleRoomSelected());
        tvTimeMovie.setText(getTitleTimeSelected());
        tvCountBooking.setText(mActivityMovieDetailBinding.edtCountBooking.getText().toString().trim());
        tvFoodDrink.setText(getStringFoodAndDrink());
        String strTotalAmount = getTotalAmount() + ConstantKey.UNIT_CURRENCY;
        tvTotalAmount.setText(strTotalAmount);


        // Set listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogOk.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                long id = System.currentTimeMillis();
                BookingHistory bookingHistory = new BookingHistory(id, mMovie.getName(),
                        mMovie.getDate(), getTitleRoomSelected(), getTitleTimeSelected(),
                        tvCountBooking.getText().toString(), getStringFoodAndDrink(), strTotalAmount);

                MyApplication.get(MovieDetailActivity.this).getBookingDatabaseReference()
                        .child(GlobalFuntion.getStringEmailUser()).child(String.valueOf(id))
                        .setValue(bookingHistory, (error, ref) -> {
                            Toast.makeText(MovieDetailActivity.this,
                                    getString(R.string.msg_booking_movie_success), Toast.LENGTH_LONG).show();
                            GlobalFuntion.hideSoftKeyboard(MovieDetailActivity.this);
                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }

    private List<Food> getListFoodSelected() {
        List<Food> listFoodSelected = new ArrayList<>();
        if (mListFood != null) {
            for (Food food : mListFood) {
                if(food.getCount() > 0) {
                    listFoodSelected.add(food);
                }
            }
        }
        return listFoodSelected;
    }

    private String getStringFoodAndDrink() {
        String result = "";
        List<Food> listFoodSelected = getListFoodSelected();
        if (listFoodSelected.isEmpty()) {
            return "Không";
        }
        for (Food food : listFoodSelected) {
            if (StringUtil.isEmpty(result)) {
                result = food.getName() + " (" + food.getPrice()
                        + ConstantKey.UNIT_CURRENCY + ")"
                        + " - Số lượng: " + food.getCount();
            } else {
                result = result + "\n"
                        + food.getName() + " (" + food.getPrice()
                        + ConstantKey.UNIT_CURRENCY + ")"
                        + " - Số lượng: " + food.getCount();
            }
        }

        return result;
    }

    private int getTotalAmount() {
        if (mMovie == null) {
            return 0;
        }
        int countBooking = 0;
        try {
            countBooking = Integer.parseInt(mActivityMovieDetailBinding.edtCountBooking.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int priceMovie = countBooking * mMovie.getPrice();

        int priceFoodDrink = 0;
        List<Food> listFoodSelected = getListFoodSelected();
        if (!listFoodSelected.isEmpty()) {
            for (Food food : listFoodSelected) {
                priceFoodDrink = priceFoodDrink + food.getPrice() * food.getCount();
            }
        }

        return priceMovie + priceFoodDrink;
    }
}