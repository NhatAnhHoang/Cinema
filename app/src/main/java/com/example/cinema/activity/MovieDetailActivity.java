package com.example.cinema.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
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
import com.example.cinema.adapter.SeatAdapter;
import com.example.cinema.adapter.TimeAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.ActivityMovieDetailBinding;
import com.example.cinema.listener.IOnSingleClickListener;
import com.example.cinema.model.BookingHistory;
import com.example.cinema.model.Food;
import com.example.cinema.model.Movie;
import com.example.cinema.model.Room;
import com.example.cinema.model.Seat;
import com.example.cinema.model.SeatLocal;
import com.example.cinema.model.SlotTime;
import com.example.cinema.util.StringUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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

    private List<SeatLocal> mListSeats;
    private SeatAdapter mSeatAdapter;


    private PlayerView mExoPlayerView;
    private ExtractorMediaSource mMediaSource;
    private SimpleExoPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMovieDetailBinding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityMovieDetailBinding.getRoot());

        getDataIntent();
        displayDataMovie();

        mActivityMovieDetailBinding.imgBack.setOnClickListener(view -> onBackPressed());
        mActivityMovieDetailBinding.btnWatchTrailer.setOnClickListener(view -> scrollToLayoutTrailer());
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
            initExoPlayer();
            mActivityMovieDetailBinding.imgPlayMovie.setOnClickListener(view -> startVideo());
        }

        showListRooms();
        showListTimes();
        showListSeats();
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

    private void showListSeats() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        mActivityMovieDetailBinding.rcvSeat.setLayoutManager(gridLayoutManager);

        mListSeats = getListSeatLocal();
        mSeatAdapter = new SeatAdapter(mListSeats, this::onClickItemSeat);
        mActivityMovieDetailBinding.rcvSeat.setAdapter(mSeatAdapter);
    }

    private List<SeatLocal> getListSeatLocal() {
        List<SeatLocal> list = new ArrayList<>();
        if (mMovie.getSeats() != null) {
            for (Seat seat : mMovie.getSeats()) {
                SeatLocal seatLocal = new SeatLocal(seat.getId(), seat.getTitle(), seat.isSelected());
                list.add(seatLocal);
            }
        }
        return list;
    }

    private Seat getSeatFromId(int id) {
        Seat seatResult = new Seat();
        if (mMovie.getSeats() != null) {
            for (Seat seat : mMovie.getSeats()) {
                if (seat.getId() == id) {
                    seatResult = seat;
                    break;
                }
            }
        }
        return seatResult;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onClickItemSeat(SeatLocal seat) {
        if (seat.isSelected()) {
            return;
        }
        seat.setChecked(!seat.isChecked());
        mSeatAdapter.notifyDataSetChanged();
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
        int countBooking = Integer.parseInt(strCountBooking);
        int countSeat = getListSeatChecked().size();
        if (countBooking != countSeat) {
            Toast.makeText(this, getString(R.string.msg_count_seat_not_match), Toast.LENGTH_SHORT).show();
            return;
        }

        setListSeatUpdate();

        showDialogConfirmBooking();
    }

    private void setListSeatUpdate() {
        for (SeatLocal seatChecked : getListSeatChecked()) {
            getSeatFromId(seatChecked.getId()).setSelected(true);
        }
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
        final TextView tvCountSeat = dialog.findViewById(R.id.tv_count_seat);
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
        tvCountSeat.setText(getStringSeatChecked());
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
                MyApplication.get(MovieDetailActivity.this).getMovieDatabaseReference()
                        .child(String.valueOf(mMovie.getId())).setValue(mMovie, (error, ref) -> {
                    long id = System.currentTimeMillis();
                    BookingHistory bookingHistory = new BookingHistory(id, mMovie.getName(),
                            mMovie.getDate(), getTitleRoomSelected(), getTitleTimeSelected(),
                            tvCountBooking.getText().toString(), getStringSeatChecked(),
                            getStringFoodAndDrink(), strTotalAmount);

                    MyApplication.get(MovieDetailActivity.this).getBookingDatabaseReference()
                            .child(GlobalFuntion.getStringEmailUser()).child(String.valueOf(id))
                            .setValue(bookingHistory, (error1, ref1) -> {
                                Toast.makeText(MovieDetailActivity.this,
                                        getString(R.string.msg_booking_movie_success), Toast.LENGTH_LONG).show();
                                GlobalFuntion.hideSoftKeyboard(MovieDetailActivity.this);
                                dialog.dismiss();
                            });
                        });
            }
        });

        dialog.show();
    }

    private List<SeatLocal> getListSeatChecked() {
        List<SeatLocal> listSeatChecked = new ArrayList<>();
        if (mListSeats != null) {
            for (SeatLocal seat : mListSeats) {
                if (seat.isChecked()) {
                    listSeatChecked.add(seat);
                }
            }
        }
        return listSeatChecked;
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

    private String getStringSeatChecked() {
        String result = "";
        List<SeatLocal> listSeatChecked = getListSeatChecked();
        for (SeatLocal seatLocal : listSeatChecked) {
            if (StringUtil.isEmpty(result)) {
                result = seatLocal.getTitle();
            } else {
                result = result + ", " + seatLocal.getTitle();
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

    private void scrollToLayoutTrailer() {
        long dulation = 500;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            float y = mActivityMovieDetailBinding.labelMovieTrailer.getY();
            ScrollView sv = mActivityMovieDetailBinding.scrollView;
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(sv, "scrollY", 0, (int)y);
            objectAnimator.start();

            startVideo();
        }, dulation);
    }

    private void initExoPlayer() {
        mExoPlayerView = mActivityMovieDetailBinding.exoplayer;

        if (mPlayer != null) {
            return;
        }
        String userAgent = Util.getUserAgent(this, this.getApplicationInfo().packageName);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent,
                null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                null, httpDataSourceFactory);
        mMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mMovie.getUrl()));

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        mPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                trackSelector, loadControl);
        mPlayer.addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                // Do nothing
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                // Do nothing
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                // Do nothing
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // Do nothing
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                // Do nothing
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                // Do nothing
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                // Do nothing
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                // Do nothing
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                // Do nothing
            }

            @Override
            public void onSeekProcessed() {
                // Do nothing
            }
        });
        // Set player
        mExoPlayerView.setPlayer(mPlayer);
        mExoPlayerView.hideController();
    }

    private void startVideo() {
        mActivityMovieDetailBinding.imgPlayMovie.setVisibility(View.GONE);
        if (mPlayer != null) {
            // Prepare video source
            mPlayer.prepare(mMediaSource);
            // Set play video
            mPlayer.setPlayWhenReady(true);
        }
    }
}