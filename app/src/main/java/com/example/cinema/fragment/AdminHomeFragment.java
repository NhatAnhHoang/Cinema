package com.example.cinema.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cinema.MyApplication;
import com.example.cinema.R;
import com.example.cinema.activity.AddMovieActivity;
import com.example.cinema.adapter.MovieAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentHomeAdminBinding;
import com.example.cinema.model.Movie;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private FragmentHomeAdminBinding mFragmentHomeAdminBinding;
    private List<Movie> mListMovies;
    private MovieAdapter mMovieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeAdminBinding = FragmentHomeAdminBinding.inflate(inflater, container, false);
        initView();
        getListMovies();
        return mFragmentHomeAdminBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentHomeAdminBinding.rcvMovie.setLayoutManager(linearLayoutManager);

        mListMovies = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(getActivity(), mListMovies, new MovieAdapter.IManagerMovieListener() {
            @Override
            public void editMovie(Movie movie) {
                onClickEditMovie(movie);
            }

            @Override
            public void deleteMovie(Movie movie) {
                deleteMovieItem(movie);
            }

            @Override
            public void clickItemMovie(Movie movie) {

            }
        });
        mFragmentHomeAdminBinding.rcvMovie.setAdapter(mMovieAdapter);

        mFragmentHomeAdminBinding.btnAddMovie.setOnClickListener(v -> onClickAddMovie());
    }

    private void onClickAddMovie() {
        GlobalFuntion.startActivity(getActivity(), AddMovieActivity.class);
    }

    private void onClickEditMovie(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.KEY_INTENT_MOVIE_OBJECT, movie);
        GlobalFuntion.startActivity(getActivity(), AddMovieActivity.class, bundle);
    }

    private void deleteMovieItem(Movie movie) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getMovieDatabaseReference()
                            .child(String.valueOf(movie.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.msg_delete_movie_successfully), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public void getListMovies() {
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
                        mListMovies.add(0, movie);
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
                        for (Movie movieObject : mListMovies) {
                            if (movie.getId() == movieObject.getId()) {
                                mListMovies.remove(movieObject);
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
}
