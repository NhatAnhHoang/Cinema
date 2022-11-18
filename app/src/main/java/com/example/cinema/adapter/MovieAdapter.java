package com.example.cinema.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinema.R;
import com.example.cinema.databinding.ItemMovieBinding;
import com.example.cinema.model.Movie;
import com.example.cinema.util.StringUtil;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private final List<Movie> mListMovies;
    private final IManagerMovieListener iManagerMovieListener;

    public interface IManagerMovieListener {
        void clickItemMovie(Movie movie);
    }

    public MovieAdapter(Context mContext, List<Movie> mListMovies, IManagerMovieListener iManagerMovieListener) {
        this.mContext = mContext;
        this.mListMovies = mListMovies;
        this.iManagerMovieListener = iManagerMovieListener;
    }

    public void release() {
        if (mContext != null) {
            mContext = null;
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieBinding itemMovieBinding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(itemMovieBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mListMovies.get(position);
        if (movie == null) {
            return;
        }
        if (!StringUtil.isEmpty(movie.getImage())) {
            Glide.with(mContext).load(movie.getImage()).error(R.drawable.img_no_available).into(holder.mItemMovieBinding.imgMovie);
        } else {
            holder.mItemMovieBinding.imgMovie.setImageResource(R.drawable.img_no_available);
        }
        holder.mItemMovieBinding.tvName.setText(movie.getName());

        holder.mItemMovieBinding.layoutItem.setOnClickListener(v -> iManagerMovieListener.clickItemMovie(movie));
    }

    @Override
    public int getItemCount() {
        if (mListMovies != null) {
            return mListMovies.size();
        }
        return 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding mItemMovieBinding;

        public MovieViewHolder(@NonNull ItemMovieBinding itemMovieBinding) {
            super(itemMovieBinding.getRoot());
            this.mItemMovieBinding = itemMovieBinding;
        }
    }
}
