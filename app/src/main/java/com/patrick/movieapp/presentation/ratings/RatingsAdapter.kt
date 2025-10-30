package com.patrick.movieapp.presentation.ratings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.patrick.movieapp.BuildConfig
import com.patrick.movieapp.R
import com.patrick.movieapp.data.remote.dto.RatingResponse
import com.patrick.movieapp.databinding.ItemRatingBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RatingsAdapter(
    private val onMovieClick: (RatingResponse) -> Unit,
    private val onDeleteClick: (RatingResponse) -> Unit,
    private val movieTitles: Map<Int, String> = emptyMap(),
    private val moviePosters: Map<Int, String> = emptyMap()
) : ListAdapter<RatingResponse, RatingsAdapter.RatingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val binding = ItemRatingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RatingViewHolder(binding, onMovieClick, onDeleteClick, movieTitles, moviePosters)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RatingViewHolder(
        private val binding: ItemRatingBinding,
        private val onMovieClick: (RatingResponse) -> Unit,
        private val onDeleteClick: (RatingResponse) -> Unit,
        private val movieTitles: Map<Int, String>,
        private val moviePosters: Map<Int, String>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: RatingResponse) {
            binding.apply {
                // Título de la película
                val movieTitle = movieTitles[rating.movieId] ?: "Película #${rating.movieId}"
                tvMovieTitle.text = movieTitle

                // Póster de la película
                val posterPath = moviePosters[rating.movieId]
                val posterUrl = if (posterPath != null) {
                    "${BuildConfig.TMDB_IMAGE_URL}w185$posterPath"
                } else {
                    null
                }

                Glide.with(ivMoviePoster)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_movie_placeholder)
                    .centerCrop()
                    .into(ivMoviePoster)

                // Mostrar estrellas según la calificación
                val stars = "⭐".repeat(rating.rating.toInt())
                tvRating.text = "$stars ${rating.rating}"

                // Review
                if (rating.review.isNullOrEmpty()) {
                    tvReview.visibility = android.view.View.GONE
                } else {
                    tvReview.visibility = android.view.View.VISIBLE
                    tvReview.text = "\"${rating.review}\""
                }

                // Fecha
                try {
                    val dateTime = LocalDateTime.parse(rating.watchedAt)
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    tvDate.text = dateTime.format(formatter)
                } catch (e: Exception) {
                    tvDate.text = rating.watchedAt
                }

                // Click en la card
                root.setOnClickListener {
                    onMovieClick(rating)
                }

                // Click en el botón de eliminar
                btnDelete.setOnClickListener {
                    onDeleteClick(rating)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RatingResponse>() {
        override fun areItemsTheSame(oldItem: RatingResponse, newItem: RatingResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RatingResponse, newItem: RatingResponse): Boolean {
            return oldItem == newItem
        }
    }
}