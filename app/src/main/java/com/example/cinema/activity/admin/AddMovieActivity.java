package com.example.cinema.activity.admin;

import android.os.Bundle;
import android.widget.Toast;

import com.example.cinema.MyApplication;
import com.example.cinema.R;
import com.example.cinema.activity.BaseActivity;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.ActivityAddMovieBinding;
import com.example.cinema.model.Movie;
import com.example.cinema.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AddMovieActivity extends BaseActivity {

    private ActivityAddMovieBinding mActivityAddMovieBinding;
    private boolean isUpdate;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddMovieBinding = ActivityAddMovieBinding.inflate(getLayoutInflater());
        setContentView(mActivityAddMovieBinding.getRoot());

        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mMovie = (Movie) bundleReceived.get(ConstantKey.KEY_INTENT_MOVIE_OBJECT);
        }

        initView();

        mActivityAddMovieBinding.imgBack.setOnClickListener(v -> onBackPressed());
        mActivityAddMovieBinding.btnAddOrEdit.setOnClickListener(v -> addOrEditMovie());
        mActivityAddMovieBinding.tvDate.setOnClickListener(v ->
                GlobalFuntion.showDatePicker(this, date -> mActivityAddMovieBinding.tvDate.setText(date)));
    }

    private void initView() {
        if (isUpdate) {
            mActivityAddMovieBinding.tvTitle.setText(getString(R.string.edit_movie_title));
            mActivityAddMovieBinding.btnAddOrEdit.setText(getString(R.string.action_edit));

            mActivityAddMovieBinding.edtName.setText(mMovie.getName());
            mActivityAddMovieBinding.edtDescription.setText(mMovie.getDescription());
            mActivityAddMovieBinding.edtPrice.setText(String.valueOf(mMovie.getPrice()));
            mActivityAddMovieBinding.tvDate.setText(mMovie.getDate());
            mActivityAddMovieBinding.edtImage.setText(mMovie.getImage());
            mActivityAddMovieBinding.edtVideo.setText(mMovie.getUrl());
        } else {
            mActivityAddMovieBinding.tvTitle.setText(getString(R.string.add_movie_title));
            mActivityAddMovieBinding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditMovie() {
        String strName = mActivityAddMovieBinding.edtName.getText().toString().trim();
        String strDescription = mActivityAddMovieBinding.edtDescription.getText().toString().trim();
        String strPrice = mActivityAddMovieBinding.edtPrice.getText().toString().trim();
        String strDate = mActivityAddMovieBinding.tvDate.getText().toString().trim();
        String strImage = mActivityAddMovieBinding.edtImage.getText().toString().trim();
        String strVideo = mActivityAddMovieBinding.edtVideo.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_movie_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strDescription)) {
            Toast.makeText(this, getString(R.string.msg_description_movie_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_movie_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strDate)) {
            Toast.makeText(this, getString(R.string.msg_date_movie_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_movie_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strVideo)) {
            Toast.makeText(this, getString(R.string.msg_video_movie_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update phim
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("description", strDescription);
            map.put("price", Integer.parseInt(strPrice));
            map.put("date", strDate);
            map.put("image", strImage);
            map.put("url", strVideo);

            MyApplication.get(this).getMovieDatabaseReference()
                    .child(String.valueOf(mMovie.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false);
                Toast.makeText(AddMovieActivity.this, getString(R.string.msg_edit_movie_successfully), Toast.LENGTH_SHORT).show();
                GlobalFuntion.hideSoftKeyboard(this);
            });
            return;
        }

        // Add phim
        showProgressDialog(true);
        long movieId = System.currentTimeMillis();
        Movie movie = new Movie(movieId, strName, strDescription, Integer.parseInt(strPrice),
                strDate, strImage, strVideo, GlobalFuntion.getListRooms());
        MyApplication.get(this).getMovieDatabaseReference().child(String.valueOf(movieId)).setValue(movie, (error, ref) -> {
            showProgressDialog(false);
            mActivityAddMovieBinding.edtName.setText("");
            mActivityAddMovieBinding.edtDescription.setText("");
            mActivityAddMovieBinding.edtPrice.setText("");
            mActivityAddMovieBinding.tvDate.setText("");
            mActivityAddMovieBinding.edtImage.setText("");
            mActivityAddMovieBinding.edtVideo.setText("");
            GlobalFuntion.hideSoftKeyboard(this);
            Toast.makeText(this, getString(R.string.msg_add_movie_successfully), Toast.LENGTH_SHORT).show();
        });
    }
}