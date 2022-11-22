package com.example.cinema.fragment;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cinema.MyApplication;
import com.example.cinema.activity.MovieDetailActivity;
import com.example.cinema.adapter.MovieAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentHomeBinding;
import com.example.cinema.model.Movie;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;
    private List<Movie> mListMovies;
    private MovieAdapter mMovieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        getListMovies("");
        return mFragmentHomeBinding.getRoot();
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mFragmentHomeBinding.rcvMovie.setLayoutManager(gridLayoutManager);
        mListMovies = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getActivity(), mListMovies, movie -> {
            goToMovieDetail(movie);
        });
        mFragmentHomeBinding.rcvMovie.setAdapter(mMovieAdapter);

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchMovie());

        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovie();
                return true;
            }
            return false;
        });

        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mListMovies.clear();
                    getListMovies("");
                }
            }
        });
    }

    private void goToMovieDetail(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.KEY_INTENT_MOVIE_OBJECT, movie);
        GlobalFuntion.startActivity(getActivity(), MovieDetailActivity.class, bundle);
    }

    private void searchMovie() {
        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        mListMovies.clear();
        getListMovies(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    private void getListMovies(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getMovieDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || mListMovies == null || mMovieAdapter == null) {
                            return;
                        }

                        if (key == null || key.equals("")) {
                            mListMovies.add(0, movie);
                        } else {
                            if (movie.getName().toLowerCase().trim().contains(key.toLowerCase().trim())) {
                                mListMovies.add(0, movie);
                            }
                        }

                        mMovieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || mListMovies == null || mListMovies.isEmpty() || mMovieAdapter == null) {
                            return;
                        }
                        for (Movie movieEntity : mListMovies) {
                            if (movie.getId() == movieEntity.getId()) {
                                movieEntity.setName(movie.getName());
                                movieEntity.setDescription(movie.getDescription());
                                movieEntity.setPrice(movie.getPrice());
                                movieEntity.setDate(movie.getDate());
                                movieEntity.setImage(movie.getImage());
                                movieEntity.setUrl(movie.getUrl());
                                movieEntity.setSeats(movie.getSeats());
                                break;
                            }
                        }
                        mMovieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || mListMovies == null || mListMovies.isEmpty() || mMovieAdapter == null) {
                            return;
                        }
                        for (Movie movieDelete : mListMovies) {
                            if (movie.getId() == movieDelete.getId()) {
                                mListMovies.remove(movieDelete);
                                break;
                            }
                        }
                        mMovieAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMovieAdapter != null) {
            mMovieAdapter.release();
        }
    }
}
