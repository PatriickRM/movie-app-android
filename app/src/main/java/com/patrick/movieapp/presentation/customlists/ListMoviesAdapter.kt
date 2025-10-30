package com.patrick.movieapp.presentation.customlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.remote.dto.ListMovieResponse
import com.patrick.movieapp.databinding.ItemListMovieBinding

class ListMoviesAdapter(
    private val onMovieClick: (ListMovieResponse) -> Unit,
    private val onRemoveClick: (ListMovieResponse) -> Unit
) : ListAdapter<ListMovieResponse, ListMoviesAdapter.MovieViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemListMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onMovieClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(
        private val binding: ItemListMovieBinding,
        private val onMovieClick: (ListMovieResponse) -> Unit,
        private val onRemoveClick: (ListMovieResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: ListMovieResponse) {
            binding.apply {
                tvMovieTitle.text = movie.movieTitle ?: "Sin título"

                val posterUrl = if (movie.moviePoster != null) {
                    "${BuildConfig.TMDB_IMAGE_URL}w500${movie.moviePoster}"
                } else {
                    null
                }

                Glide.with(ivMoviePoster)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_movie_placeholder)
                    .into(ivMoviePoster)

                // Click en la card
                root.setOnClickListener {
                    onMovieClick(movie)
                }

                // Click en el botón de eliminar
                btnRemoveMovie.setOnClickListener {
                    onRemoveClick(movie)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ListMovieResponse>() {
        override fun areItemsTheSame(oldItem: ListMovieResponse, newItem: ListMovieResponse): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: ListMovieResponse, newItem: ListMovieResponse): Boolean {
            return oldItem == newItem
        }
    }
}