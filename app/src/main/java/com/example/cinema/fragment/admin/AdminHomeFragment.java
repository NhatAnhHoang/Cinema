package com.example.cinema.fragment.admin;

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
import com.example.cinema.activity.admin.AddMovieActivity;
import com.example.cinema.adapter.admin.AdminMovieAdapter;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.constant.GlobalFuntion;
import com.example.cinema.databinding.FragmentAdminHomeBinding;
import com.example.cinema.model.Movie;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private FragmentAdminHomeBinding mFragmentAdminHomeBinding;
    private List<Movie> mListMovies;
    private AdminMovieAdapter mAdminMovieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminHomeBinding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        initView();
        getListMovies();
        return mFragmentAdminHomeBinding.getRoot();
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminHomeBinding.rcvMovie.setLayoutManager(linearLayoutManager);

        mListMovies = new ArrayList<>();
        mAdminMovieAdapter = new AdminMovieAdapter(getActivity(), mListMovies, new AdminMovieAdapter.IManagerMovieListener() {
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
        mFragmentAdminHomeBinding.rcvMovie.setAdapter(mAdminMovieAdapter);

        mFragmentAdminHomeBinding.btnAddMovie.setOnClickListener(v -> onClickAddMovie());
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
                        if (movie == null || mListMovies == null || mAdminMovieAdapter == null) {
                            return;
                        }
                        mListMovies.add(0, movie);
                        mAdminMovieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || mListMovies == null
                                || mListMovies.isEmpty() || mAdminMovieAdapter == null) {
                            return;
                        }
                        for (int i = 0; i < mListMovies.size(); i++) {
                            Movie movieEntity = mListMovies.get(i);
                            if (movie.getId() == movieEntity.getId()) {
                                mListMovies.set(i, movie);
                                break;
                            }
                        }
                        mAdminMovieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || mListMovies == null
                                || mListMovies.isEmpty() || mAdminMovieAdapter == null) {
                            return;
                        }
                        for (Movie movieObject : mListMovies) {
                            if (movie.getId() == movieObject.getId()) {
                                mListMovies.remove(movieObject);
                                break;
                            }
                        }
                        mAdminMovieAdapter.notifyDataSetChanged();
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
        if (mAdminMovieAdapter != null) {
            mAdminMovieAdapter.release();
        }
    }
}
