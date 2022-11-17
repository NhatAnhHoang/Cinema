package com.example.cinema.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinema.R;
import com.example.cinema.constant.ConstantKey;
import com.example.cinema.databinding.ItemMovieBinding;
import com.example.cinema.model.Movie;
import com.example.cinema.util.StringUtil;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final Context mContext;
    private final List<Movie> mListMovies;
    private final IManagerMovieListener iManagerMovieListener;

    public interface IManagerMovieListener {
        void editMovie(Movie movie);

        void deleteMovie(Movie movie);

        void clickItemMovie(Movie movie);
    }

    public MovieAdapter(Context mContext, List<Movie> mListMovies, IManagerMovieListener iManagerMovieListener) {
        this.mContext = mContext;
        this.mListMovies = mListMovies;
        this.iManagerMovieListener = iManagerMovieListener;
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
        holder.mItemMovieBinding.tvDescription.setText(movie.getDescription());
        String strPrice = movie.getPrice() + ConstantKey.UNIT_CURRENCY_MOVIE;
        holder.mItemMovieBinding.tvPrice.setText(strPrice);
        holder.mItemMovieBinding.tvDate.setText(movie.getDate());

        holder.mItemMovieBinding.imgEdit.setOnClickListener(v -> iManagerMovieListener.editMovie(movie));
        holder.mItemMovieBinding.imgDelete.setOnClickListener(v -> iManagerMovieListener.deleteMovie(movie));
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
