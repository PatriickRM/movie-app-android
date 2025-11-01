package com.patrick.movieapp.presentation.ai

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.patrick.movieapp.databinding.ItemAiRecommendationBinding
import com.patrick.movieapp.data.remote.dto.tmdb.MovieRecommendation

class AIRecommendationsAdapter(
    private val onMovieClick: (Int) -> Unit
) : ListAdapter<MovieRecommendation, AIRecommendationsAdapter.RecommendationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val binding = ItemAiRecommendationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecommendationViewHolder(binding, onMovieClick)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    class RecommendationViewHolder(
        private val binding: ItemAiRecommendationBinding,
        private val onMovieClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recommendation: MovieRecommendation, position: Int) {
            binding.apply {
                tvPosition.text = "$position"
                tvMovieTitle.text = recommendation.title
                tvReason.text = recommendation.reason

                root.setOnClickListener {
                    Log.d("AIAdapter", "Clicked movie ID: ${recommendation.movieId}, Title: ${recommendation.title}")
                    onMovieClick(recommendation.movieId)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieRecommendation>() {
        override fun areItemsTheSame(oldItem: MovieRecommendation, newItem: MovieRecommendation): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: MovieRecommendation, newItem: MovieRecommendation): Boolean {
            return oldItem == newItem
        }
    }
}