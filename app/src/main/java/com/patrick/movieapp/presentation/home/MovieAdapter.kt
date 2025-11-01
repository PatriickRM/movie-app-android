package com.patrick.movieapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.remote.dto.tmdb.TMDbMovie
import com.patrick.movieapp.databinding.ItemMovieBinding

class MovieAdapter(
    private val onMovieClick: (TMDbMovie) -> Unit
) : ListAdapter<TMDbMovie, MovieAdapter.MovieViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onMovieClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(
        private val binding: ItemMovieBinding,
        private val onMovieClick: (TMDbMovie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: TMDbMovie) {
            binding.apply {
                tvMovieTitle.text = movie.title
                tvMovieRating.text = String.format("⭐ %.1f", movie.voteAverage)

                val posterUrl = if (movie.posterPath != null) {
                    "${BuildConfig.TMDB_IMAGE_URL}w500${movie.posterPath}"
                } else {
                    null
                }

                Glide.with(ivMoviePoster)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_movie_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivMoviePoster)

                root.setOnClickListener {
                    onMovieClick(movie)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TMDbMovie>() {
        override fun areItemsTheSame(oldItem: TMDbMovie, newItem: TMDbMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TMDbMovie, newItem: TMDbMovie): Boolean {
            return oldItem == newItem
        }
    }
}
