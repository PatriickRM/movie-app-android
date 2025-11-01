package com.patrick.movieapp.presentation.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.remote.dto.FavoriteResponse
import com.patrick.movieapp.databinding.ItemFavoriteBinding

class FavoritesAdapter(
    private val onMovieClick: (FavoriteResponse) -> Unit,
    private val onRemoveClick: (FavoriteResponse) -> Unit
) : ListAdapter<FavoriteResponse, FavoritesAdapter.FavoriteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding, onMovieClick, onRemoveClick)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding,
        private val onMovieClick: (FavoriteResponse) -> Unit,
        private val onRemoveClick: (FavoriteResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoriteResponse) {
            binding.apply {
                tvMovieTitle.text = favorite.movieTitle ?: "Sin título"
                tvMovieRating.text = String.format("⭐ %.1f", favorite.voteAverage ?: 0.0)

                val posterUrl = if (favorite.moviePoster != null) {
                    "${BuildConfig.TMDB_IMAGE_URL}w500${favorite.moviePoster}"
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
                    onMovieClick(favorite)
                }

                // Click en el botón de eliminar
                btnRemoveFavorite.setOnClickListener {
                    onRemoveClick(favorite)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FavoriteResponse>() {
        override fun areItemsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteResponse, newItem: FavoriteResponse): Boolean {
            return oldItem == newItem
        }
    }
}